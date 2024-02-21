import Header from "layout/header/Header";
import 'basic-style.css'
import 'App.css';
import 'react-toastify/dist/ReactToastify.css';
import "css/bootstrap/bootstrap.min.css"
import "css/base.css"
import {Route, Routes, useLocation, useNavigate} from "react-router";
import {ToastContainer} from "react-toastify";
import {lazy, useEffect, useRef, useState} from "react";
import {useDispatch, useSelector} from "react-redux";
import UserInfoApi from "utill/api/userInfo/UserInfoApi";
import ReNewTokenApi from "utill/api/ReNewToken/ReNewTokenApi";
import {userActions} from "store/userInfo/userReducers";
import {authActions} from "store/auth/authReducers";
import {persistor} from "store/store";
import {Profile} from "content/profile/Profile";
import * as StompJs from "@stomp/stompjs";
import ReactPlayer from "react-player";

// const Feed = lazy(() => import('./routes/Feed'));
// const Home = lazy(() => import('./routes/home'));
// const UserId = lazy(() => import('./routes/userId'));

function App() {
  const currentAuth = useSelector((state) => state?.authReducer);
  const dispatch = useDispatch();
  const bc = new BroadcastChannel(`my_chanel`);
  const location = useLocation();
  const client = useRef({client:null});

  function connect(accessToken) {
    const clientData = new StompJs.Client({
      brokerURL: "ws://localhost:8080/webSocket",
      connectHeaders: {
        Authorization: accessToken,
      }, debug: function (message) {
      },
      // reconnectDelay: 5000, // 자동 재 연결
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    })
    clientData.onConnect = function () {
      // 구독
      clientData.subscribe("/user/queue/alarm", function (message) {
        console.log("구독 : "+ message.body);
      });
      clientData.subscribe("/user/queue/push/msg", function (message) {
        console.log("push : "+ message.body);
      });
    };
    // 연결
    clientData.activate();
    clientData.publish({destination:"/app/push"});
    client.current.client = clientData;
  }
  bc.onmessage = function (e) {
    let data = e.data;
    if (data.type === "logout") {
      window.location.replace("/")
    } else {
      window.location.replace(location.pathname);
    }
  }
  async function CheckUserInfo(accessToken, refreshToken, accessHeader,
      refreshHeader) {
    try {
      if (accessToken) {
        const response = await UserInfoApi(accessHeader);
        if (response.code === 200) {
          const userData = response.data;
          dispatch(userActions.setUserId(userData));
          dispatch(userActions.setEmail(userData));
          dispatch(userActions.setPictureUrl(userData));
          dispatch(userActions.setUserName(userData));
          connect(accessToken);
          return;
        } else if (response.code === 401 && refreshToken) {
          const renewTokenResponse = await ReNewTokenApi(refreshHeader);
          if (renewTokenResponse.code === 200) {
            const newAccessToken = renewTokenResponse.data;
            dispatch(authActions.setAccess(newAccessToken));
            dispatch(authActions.setAccessHeader());
            // Recursive call to CheckUserInfo after token renewal
            await CheckUserInfo(newAccessToken, refreshToken, accessHeader,
                refreshHeader, dispatch);
            return;
          }
        }
      }
      throw new Error();
    } catch (error) {
      if (client.current.client) {
        client.current.client.deactivate();
      }
      persistor.purge();
    }
  }

  useEffect(() => {
    CheckUserInfo(currentAuth.access, currentAuth.refresh,
        currentAuth.accessHeader, currentAuth.refreshHeader);
  }, [currentAuth]) // 페이지 이동 시 유저정보 확인
  return (
      <div className="App">
        <Header bc={bc}/>
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

        {/*<ReactPlayer url={process.env.PUBLIC_URL + "/users/file/track?id=1"} width="400px" height="300px" playing={true} controls={true} />*/}
        <div className="l-container">
          <Routes>
            <Route path="/">
            </Route>
            <Route path="/feed">
            </Route>
            <Route path="/:userId" element={<Profile/>}>
            </Route>
          </Routes>
        </div>

      </div>
  );
}

export default App;
