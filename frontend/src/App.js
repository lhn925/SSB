import Header from "layout/header/Header";
import 'basic-style.css'
import 'App.css';
import 'react-toastify/dist/ReactToastify.css';
import "css/bootstrap/bootstrap.min.css"
import "css/base.css"
import {Route, Routes, useLocation, useNavigate} from "react-router";
import {ToastContainer} from "react-toastify";
import {useEffect} from "react";
import {useDispatch, useSelector} from "react-redux";
import UserInfoApi from "utill/api/userInfo/UserInfoApi";
import ReNewTokenApi from "utill/api/ReNewToken/ReNewTokenApi";
import {userActions} from "store/userInfo/userReducers";
import {authActions} from "store/auth/authReducers";
import {persistor} from "store/store";

function App() {

  const auth = useSelector((state) => state.authReducer);
  const dispatch = useDispatch();
  const location = useLocation();

  async function CheckUserInfo(accessToken, accessHeader) {
    if (accessToken != null) { // accessToken 확인
      const response = await UserInfoApi(accessHeader);
      if (response.code == 200) {
        dispatch(userActions.setUserId(response.data));
        dispatch(userActions.setEmail(response.data));
        dispatch(userActions.setPictureUrl(response.data));
        dispatch(userActions.setUserName(response.data));
      } else {
        if (response.code == 401 && auth.refresh != null) {
          const response = await ReNewTokenApi(auth.refreshHeader);
          if (response.code == 200) {
            const token = response.data.accessToken;
            const header = {Authorization: token}
            dispatch(authActions.setAccess(response.data));
            dispatch(authActions.setAccessHeader());
            CheckUserInfo(token, header);// 다시 한번 호출
          } else {
            persistor.purge();
          }
        } else {
          persistor.purge();
        }
      }
    }

  }

  useEffect(() => {
    CheckUserInfo(auth.access, auth.accessHeader);
  }, [location]) // 페이지 이동 시 유저정보 확인

  return (
      <div className="App">
        <Header/>

        <ToastContainer
            position="top-right"
            autoClose={3000}
            hideProgressBar={false}
            newestOnTop={false}
            closeOnClick
            rtl={false}
            pauseOnFocusLoss
            draggable
            pauseOnHover
            theme="light"
        />
        <div className="container">
          <Routes>
            <Route path="/">
            </Route>
            <Route path="/feed">
            </Route>
          </Routes>
        </div>
      </div>
  );
}

export default App;
