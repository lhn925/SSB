import Header from "layout/header/Header";
import 'basic-style.css'
import 'App.css';
import 'react-toastify/dist/ReactToastify.css';
import "css/bootstrap/bootstrap.min.css"
import "css/base.css"
import {Route, Routes, useLocation, useNavigate} from "react-router";
import {ToastContainer} from "react-toastify";
import {
  createContext,
  lazy,
  Suspense,
  useContext,
  useEffect,
  useMemo,
  useRef,
  useState
} from "react";
import {useDispatch, useSelector} from "react-redux";
import {userActions} from "store/userInfo/userReducers";
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
import {createUploadActions} from "utill/function";
import * as StompJs from "@stomp/stompjs";
import {TempRemoveApi} from "./utill/api/upload/TempRemoveApi";
import CaptchaApi from "./utill/api/captcha/CaptchaApi";

// React Lazy 는 import 하려는 컴포넌트가 defaul export 되었다는 전제하에 실행 되기 때문에
// named export 는 설정을 따로 해주어야 한다

const Profile = lazy(() => import('./content/profile/Profile').then(module => ({
  default: module.Profile
})));
const Settings = lazy(
    () => import('./content/settings/Settings').then(module => ({
      default: module.Settings
    })));

export const UploadValueContext = createContext();
export const UploadActionsContext = createContext();

function App() {
  const [coverImgFiles, setCoverImgFiles] = useState({
    tracks: [],
    playList: null
  });
  // useMemo 로 감싸지 않으면 CounterProvider 가 리렌더링이 될 때마다 새로운 배열을 만들기 때문에
  // useContext 를 사용하는 컴포넌트 쪽에서 Context 의 값이 바뀐 것으로 간주하게 되어 낭비 렌더링이 발생
  const coverImgFileActions = useMemo(() => (
      createUploadActions(coverImgFiles, setCoverImgFiles)
  ), []);

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
      dispatch(uploadInfoActions.cleanStore());
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

  // webSocket disConnect 시
  // 발생할 이벤트
  const disConnectEvent = async (uploadInfo) => {
    // 임시 트랙 삭제
    if (uploadInfo.tracks.length > 0) {
      const tracks = uploadInfo.tracks;
      const removeList = tracks.filter((track) => track.id !== 0);
      const body = {tempTrackDeleteList: removeList};
      if (removeList.length > 0) {
        const response = await TempRemoveApi(body);
        if (response.code !== 200) {
        }
      }
    }

  }

  BeforeUnload(t, uploadInfo, client.current.client);

  useEffect(() => {
    const currentClient = client.current.client;
    const handleWebSocketClose = () => {
      // 여기서 uploadInfoRef.current는 항상 최신 상태의 uploadInfo를 가리킵니다.
      disConnectEvent(uploadInfo).then(() => {
        console.log("삭제");
      })
    };
    if (currentClient !== null && uploadInfo.tracks.length > 0) {
      console.log(uploadInfo.tracks);
      currentClient.onWebSocketClose = handleWebSocketClose;
    }
    return () => {
      if (currentClient !== null && uploadInfo.tracks.length === 0) {
        console.log("close");
        // 연결 종료 시에 적절한 이벤트 핸들러 정리 로직을 추가
        currentClient.offWebSocketClose = handleWebSocketClose;
      }
    }
  }, [client.current.client, uploadInfo.tracks])

  useEffect(() => {
    if (currentAuth.access) {
      CheckUserInfo(currentAuth, userActions, client, t, dispatch, bc);
    }
  }, [currentAuth]) // 페이지 이동 시 유저정보 확인

  return (
      <div className="App">
        {/*<DndProvider backend={HTML5Backend}>*/}
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
                <UploadActionsContext.Provider value={coverImgFileActions}>
                  <UploadValueContext.Provider value={coverImgFiles}>
                    <Upload
                        dispatch={dispatch}
                        uploadInfo={uploadInfo}
                        uploadInfoActions={uploadInfoActions}/>

                  </UploadValueContext.Provider>
                </UploadActionsContext.Provider>
              }>
              </Route>
              <Route path={URL_SETTINGS + "/:root?"}
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
        {/*</DndProvider>*/}
      </div>
  )
      ;
}

function CheckUserInfo(currentAuth, userActions, client, t, dispatch, bc) {
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
    Connect(client, currentAuth.access, currentAuth.refresh, userData.userId, t,
        bc);
  }).catch(() => {
    if (client.current.client) {
      persistor.purge().then(() => {
        client.current.client.deactivate()
      });
    }
  });
}

function Connect(client, accessToken, refreshToken, userId, t, bc) {
  const clientData = new StompJs.Client({
    brokerURL: `${process.env.REACT_APP_WS_URL}`,
    connectHeaders: {
      Authorization: accessToken,
    }, debug: function (message) {
    }, onStompError: function (message) {
      console.log("")
    },
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
  })

  clientData.onConnect = function () {
    //  구독
    clientData.subscribe("/topic/push/" + userId, function (message) {
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
  // client.current.id = 0;
}

function BeforeUnload(t, uploadInfo, client) {
  const handleBeforeUnload = (event) => {
    event.preventDefault();
    const message = t(`msg.common.beforeunload`);
    event.returnValue = message;
    return message;
  };

  const handleUnload = (event) => {
    event.preventDefault();
    const message = t(`msg.common.beforeunload`);
    event.returnValue = message;
    // webSocket 강제 종료
    client.deactivate();
    return message;
  };

  useEffect(() => {
    // 업로드중인 track 이 있으면 Event 발생
    if (uploadInfo.tracks.length > 0 && client !== null) {
      window.addEventListener('beforeunload', handleBeforeUnload);
      window.addEventListener('unload', handleUnload);
    }
    return () => {
      window.removeEventListener('beforeunload', handleBeforeUnload);
      window.removeEventListener('unload', handleUnload);
    };
  }, [uploadInfo]);
}

export function UseUploadValue() {
  return useContext(UploadValueContext);
}

export function UseUploadActions() {
  const value = useContext(UploadActionsContext);
  if (value === undefined) {
    throw new Error('useModalActions should be used within ModalProvider');
  }
  return value;
}

export default App;
