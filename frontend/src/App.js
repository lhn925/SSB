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
  useEffect,
  useMemo,
  useRef,
  useState
} from "react";
import {useDispatch} from "react-redux";
import {userActions} from "store/userInfo/userReducers";
import {useTranslation} from "react-i18next";
import {URL_SETTINGS} from "content/UrlEndpoints";
import {Upload} from "content/upload/Upload";
import ModalContent from "modal/content/ModalContent";
import {uploadInfoActions} from "store/upload/uploadInfo";
import "css/selectBox.css"
import {
  createUploadActions
} from "utill/function";
import TrackPlayerContainer from "content/trackplayer/TrackPlayerContainer";
import {v4 as uuidV4} from "uuid";
import {
  BeforeUnload, BroadCast,
  CheckUserInfo,
  disConnectEvent
} from "utill/app/functions";
import useModal from "hoks/modal/useModal";
import useTrackPlayer from "hoks/trackPlayer/useTrackPlayer";
import useAuth from "hoks/auth/useAuth";
import useUpload from "hoks/upload/useUpload";

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
  const currentAuth = useAuth();
  const uploadInfo = useUpload();
  const bc = new BroadcastChannel(`my_chanel`);
  const dispatch = useDispatch();
  const location = useLocation();
  const navigate = useNavigate();

  const {openModal, closeModal, changeModalType, modal} = useModal();

  const {
    playingClear,
    changePlaying,
    playing
  } = useTrackPlayer();

  const {t} = useTranslation();
  const client = useRef({client: null});
  BroadCast(bc, dispatch, location, changePlaying, playing);

  BeforeUnload(t, uploadInfo, client.current.client, playingClear, changePlaying);

  useEffect(() => {
    playingClear();
    const setWebLog = () => {
      const session = sessionStorage.getItem("ssb_session");
      if (session === null) {
        sessionStorage.setItem("ssb_session", uuidV4());
      }
    }
    setWebLog();
  }, [])

  useEffect(() => {
    const currentClient = client.current.client;
    const handleWebSocketClose = () => {
      // 여기서 uploadInfoRef.current는 항상 최신 상태의 uploadInfo를 가리킵니다.
      disConnectEvent(uploadInfo).catch(
          () => console.error("disConnectEvent error"))
    };
    if (currentClient !== null && currentClient.active
        && uploadInfo.tracks.length > 0) {
      currentClient.onWebSocketClose = handleWebSocketClose;
    }
    return () => {
      if (currentClient !== null && uploadInfo.tracks.length === 0) {
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
        <div className="container justify-content-center l-container">

          <Suspense fallback="Loading...">
            <Routes>
              <Route path="/" element={<Profile/>}>
              </Route>
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

        {
            currentAuth.access && <TrackPlayerContainer bc={bc}/>
        }


      </div>
  )
      ;
}

export default App;
