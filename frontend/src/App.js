import Header from "layout/header/Header";
import 'basic-style.css'
import 'App.css';
import 'react-toastify/dist/ReactToastify.css';
import "css/bootstrap/bootstrap.min.css"
import "css/base.css"
import {Route, Routes, useLocation, useNavigate} from "react-router";
import {ToastContainer} from "react-toastify";
import {lazy, useEffect, useRef, Suspense, useState} from "react";
import {useDispatch, useSelector} from "react-redux";
import {userActions} from "store/userInfo/userReducers";
import * as StompJs from "@stomp/stompjs";
import {authApi} from "utill/api/interceptor/ApiAuthInterceptor";
import {USERS_INFO} from "utill/api/ApiEndpoints";
import {persistor} from "store/store";
import {useTranslation} from "react-i18next";

// React Lazy 는 import 하려는 컴포넌트가 defaul export 되었다는 전제하에 실행 되기 때문에
// named export 는 설정을 따로 해주어야 한다
const Profile = lazy(() => import('./content/profile/Profile').then(module => ({
  default: module.Profile
})));
const Settings = lazy(
    () => import('./content/settings/Settings').then(module => ({
      default: module.Settings
    })));

function App() {
  const currentAuth = useSelector((state) => state?.authReducer);
  const dispatch = useDispatch();
  const bc = new BroadcastChannel(`my_chanel`);
  const location = useLocation();
  const navigate = useNavigate();
  const {t} = useTranslation();

  //
  const client = useRef({client: null});

  function connect(client, accessToken, refreshToken, userId) {
    const clientData = new StompJs.Client({
      brokerURL: `${process.env.REACT_APP_WS_URL}`,
      connectHeaders: {
        Authorization: accessToken,
      }, debug: function (message) {
      }, onStompError: function (message) {
        console.log("onStompError: " + message)
      },
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    })
    clientData.onConnect = function () {
      // 구독
      clientData.subscribe("/topic/push/" + userId, function (message) {
        console.log("topic : " + message.body);
      });
      clientData.subscribe("/topic/logout/" + refreshToken, function (message) {
        persistor.purge().then(() => {
          alert(t(`msg.common.logout.request.success`));
          bc.postMessage({type: "logout"})
        });
      });
    };
    // 연결
    clientData.activate();
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

  function CheckUserInfo() {
    authApi.get(USERS_INFO).then(data => {
      const userData = data.data;
      if (client.current.client) {
        client.current.client.deactivate();
      }
      dispatch(userActions.setUserId(userData));
      dispatch(userActions.setEmail(userData));
      dispatch(userActions.setPictureUrl(userData));
      dispatch(userActions.setUserName(userData));
      connect(client, currentAuth.access, currentAuth.refresh, userData.userId);
    }).catch(() => {
      if (client.current.client) {
        persistor.purge().then(() => client.current.client.deactivate());
      }
    });
  }

  useEffect(() => {
    if (currentAuth.access) {
      CheckUserInfo();
    }
  }, [currentAuth]) // 페이지 이동 시 유저정보 확인
  return (
      <div className="App">
        <Header bc={bc} client={client.current.client} navigate={navigate}/>
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
        <div className="l-container">
          <Suspense fallback="Loading...">
            <Routes>
              {/*<Route path="/">*/}
              {/*</Route>*/}
              {/*<Route path="/feed">*/}
              {/*</Route>*/}
              <Route path="/:userName" element={

                <Profile/>
              }>
              </Route>
              <Route path={"/settings/:root?"}
                     element={
                       <Settings navigate={navigate}
                                 location={location}/>

                     }>
              </Route>
            </Routes>
          </Suspense>
        </div>


      </div>
  )
      ;
}

export default App;
