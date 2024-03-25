import Header from "layout/header/Header";
import 'basic-style.css'
import 'App.css';
import 'react-toastify/dist/ReactToastify.css';
import "css/bootstrap/bootstrap.min.css"
import "css/base.css"
import {Route, Routes, useLocation, useNavigate} from "react-router";
import {ToastContainer} from "react-toastify";
import {lazy, useEffect, useRef, Suspense} from "react";
import {useDispatch, useSelector} from "react-redux";
import {userActions} from "store/userInfo/userReducers";
import * as StompJs from "@stomp/stompjs";
import {authApi} from "utill/api/interceptor/ApiAuthInterceptor";
import {USERS_INFO} from "utill/api/ApiEndpoints";
import {persistor} from "store/store";
import {useTranslation} from "react-i18next";
import {URL_SETTINGS} from "content/UrlEndpoints";
import {Upload} from "content/upload/Upload";
import ModalContent from "modal/content/ModalContent";
import {modalActions} from "store/modalType/modalType";
import {uploadInfoActions} from "store/upload/uploadInfo";
import "css/selectBox.css"

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
  const modal = useSelector((state) => state?.modalType);
  const uploadInfo = useSelector((state) => state?.uploadInfo);
  const dispatch = useDispatch();
  const bc = new BroadcastChannel(`my_chanel`);
  const location = useLocation();
  const navigate = useNavigate();
  const {t} = useTranslation();

  const client = useRef({client: null});

  bc.onmessage = (e) => {
    let data = e.data;
    if (data.type === "logout") {
      window.location.replace("/")
    } else {
      window.location.replace(location.pathname);
    }
  }

  const closeModal = () => {
    dispatch(modalActions.closeModal());
  }
  const openModal = () => {
    dispatch(modalActions.openModal());
  }
  const changeModalType = (type) => {
    dispatch(modalActions.changeType({type: type}));
  }

  useEffect(() => {
    if (currentAuth.access) {
      CheckUserInfo(currentAuth,userActions,client,t,dispatch,bc);
    }
  }, [currentAuth]) // 페이지 이동 시 유저정보 확인
  return (
      <div className="App">
        <Header modal={modal} dispatch={dispatch}
                openModal={openModal}
                changeModalType={changeModalType}
                bc={bc} client={client.current.client} navigate={navigate}/>
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
        <div
            className="container justify-content-center l-container">

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
              <Route path="/upload" element={
                <Upload
                    dispatch={dispatch}
                    uploadInfo={uploadInfo}
                    uploadInfoActions={uploadInfoActions}
                />
              }>
              </Route>
              <Route path={ URL_SETTINGS+"/:root?"}
                     element={
                       <Settings
                           openModal={openModal}
                           dispatch={dispatch}
                           changeModalType={changeModalType}
                           modal={modal}
                           navigate={navigate}
                                 location={location}/>
                     }>
              </Route>
            </Routes>
          </Suspense>
          <ModalContent bc={bc} modalVisible={modal.visible}
                        closeModal={closeModal}/>

          </div>
      </div>
  )
      ;
}

function connect(client, accessToken, refreshToken, userId,t,bc) {
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

  client.current.id = 0;
}


function CheckUserInfo(currentAuth,userActions,client,t,dispatch,bc) {
  authApi.get(USERS_INFO).then(data => {
    const userData = data.data;
    if (client.current.client) {
      client.current.client.deactivate();
    }
    dispatch(userActions.setUserId(userData));
    dispatch(userActions.setEmail(userData));
    dispatch(userActions.setPictureUrl(userData));
    dispatch(userActions.setUserName(userData));
    dispatch(userActions.setIsLoginBlocked(userData))
    connect(client, currentAuth.access, currentAuth.refresh, userData.userId,t,bc);
  }).catch(() => {
    if (client.current.client) {
      persistor.purge().then(() => client.current.client.deactivate());
    }
  });
}
export default App;
