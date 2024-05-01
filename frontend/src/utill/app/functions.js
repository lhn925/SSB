import {authApi} from "utill/api/interceptor/ApiAuthInterceptor";
import {USERS_INFO} from "utill/api/ApiEndpoints";
import {persistor} from "store/store";
import * as StompJs from "@stomp/stompjs";
import {useContext, useEffect} from "react";
import {TempRemoveApi} from "utill/api/upload/TempRemoveApi";
import {uploadInfoActions} from "store/upload/uploadInfo";
import {UploadActionsContext, UploadValueContext} from "App";
import {SESSION_ID} from "../enum/localKeyEnum";

export function CheckUserInfo(currentAuth, userActions, client, t, dispatch,
    bc) {
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
    persistor.purge().then(() => {
      if (client.current.client) {
        client.current.client.deactivate()
      }
    });
  });
}

function Connect(client, accessToken, refreshToken, userId, t, bc) {
  const clientData = new StompJs.Client({
    brokerURL: `${process.env.REACT_APP_WS_URL}`,
    connectHeaders: {
      Authorization: accessToken,
    }, debug: function (message) {
    }, onStompError: function (message) {
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
}

export function BeforeUnload(t, uploadInfo, client, playingClear,
    changePlaying) {
  // beforeunload 이벤트 핸들러
  const handleBeforeUnload = (event) => {
    changePlaying(false);
    // chartLogSave(currentTrack, false, updateCurrPlayLog);
    if (uploadInfo.tracks.length > 0) {
      event.preventDefault();  // 기본 동작 방지
      const message = t(`msg.common.beforeunload`);
      event.returnValue = message;  // 대부분의 브라우저에서 사용자 정의 메시지 지원 안 함
      return message;
    } else {
      playingClear();  // 트랙이 없으면 재생 상태 정리
    }
  };
  // unload 이벤트 핸들러
  const handleUnload = () => {
    changePlaying(false);
    // chartLogSave(currentTrack, false, updateCurrPlayLog);
    playingClear();  // 재생 상태 정리
    if (client && client.active) {
      client.deactivate();  // WebSocket 연결 종료
    }
  };
  useEffect(() => {
    // unload와 beforeunload 이벤트를 윈도우에 추가
    window.addEventListener('beforeunload', handleBeforeUnload);
    window.addEventListener('unload', handleUnload);

    return () => {
      // 컴포넌트 언마운트 시 이벤트 리스너 제거
      window.removeEventListener('beforeunload', handleBeforeUnload);
      window.removeEventListener('unload', handleUnload);
    };
  }, [uploadInfo.tracks.length, client, playingClear]); // 의존성 배열 업데이트
}

// webSocket disConnect 시
// 발생할 이벤트
export const disConnectEvent = async (uploadInfo) => {
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

export function BroadCast(bc, dispatch, location, changePlaying,
    playing,t) {
  bc.onmessage = (e) => {
    let data = e.data;
    if (data.type === "logout") {
      dispatch(uploadInfoActions.cleanStore());
      window.location.replace("/")
    } else if (data.type === "playing") {
      if (playing.key && playing.key !== data.key) {
        changePlaying(false);
      }
    } else if (data.type === "login"){
      const currSessionId = sessionStorage.getItem(SESSION_ID);
      if (data.sessionId !== currSessionId) {
        window.location.replace(location.pathname);
      }
    }
  }
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

