import {useDispatch, useSelector} from "react-redux";
import {playingActions} from "store/trackplayer/playingReducer";
import TrackInfoApi from "utill/api/trackPlayer/TrackInfoApi";
import {currentActions} from "store/trackplayer/currentTrack";
import {toast} from "react-toastify";
import TrackPlayApi from "utill/api/trackPlayer/TrackPlayApi";
import {useEffect} from "react";
import {settingsActions} from "store/trackplayer/playerSettings";
import {localPlyActions} from "store/trackplayer/localPly";
import {playLogActions} from "../../store/trackplayer/localPlayLog";
import {HttpStatusCode} from "axios";
import {
  createPlyInfo,
  loadFromLocalStorage
} from "utill/function";
import {LOCAL_PLY_KEY} from "utill/enum/localKeyEnum";
import TrackInfoSearchListApi
  from "utill/api/trackPlayer/TrackInfoSearchListApi";
import {localPlyTracksActions} from "store/trackplayer/localPlyTracks";

const useTrackPlayer = (bc) => {
  const dispatch = useDispatch();
  const playing = useSelector(state => state?.playingReducer);
  const currentTrack = useSelector(state => state?.currentTrack);
  const playerSettings = useSelector(state => state?.playerSettings);
  const localPly = useSelector(state => state?.localPly);
  const localPlayLog = useSelector(state => state?.localPlayLog);
  const localPlyTracks = useSelector(state => state?.localPlyTracks);

  const userInfo = useSelector(state => state?.userReducer);

  const playingClear = () => {
    dispatch(playingActions.clear());
  }
  const settingsCreate = () => {
    dispatch(settingsActions.create());
  }

  const changePlayLog = (order) => {
    const data = localPly.item[localPly.playOrders[order]];
    dispatch(playLogActions.changePlayLog(
        {
          id: data.id,
          index: data.index,
          startTime: new Date().getTime(),
        }
    ));
  }
  const updatePlyTrackInfo = (trackId, key, value) => {
    dispatch(localPlyTracksActions.updatePlyTrackInfo(
        {
          id: trackId,
          key: key,
          value: value
        }
    ));
  }
  const changePlyTrackInfo = (data) => {
    dispatch(localPlyActions.changePlyTrackInfo(
        {
          data: data
        }
    ));
  }

  const updateSettings = (key, value) => {
    dispatch(settingsActions.updateSettings(
        {key: key, value: value}
    ));
  }

  const shuffleOrders = (isShuffle) => {
    dispatch(localPlyActions.shuffleOrders(
        {isShuffle: isShuffle, playIndex: playerSettings.item.order}
    ));
  }

  const localPlyAddTracks = (trackId) => {
    TrackInfoApi(trackId).then((response) => {
      response.data.userId = userInfo.userId;
      response.data.createdDateTime = new Date().getTime();
      response.data.playIndex = playerSettings.item.order;
      dispatch(localPlyActions.addTracks({data: response.data}));
      localPlyAddTrackInfo(response.data);
    }).catch((error) => {
      toast.error(error.message);
    })
  }
  const localPlyAddTrackInfo = (data) => {
    dispatch(localPlyTracksActions.addTrackInfo({data: data}));
  }

  // 로컬 플레이리스트 정보 최신화
  const localPlyCreate = () => {
    const localPly = loadFromLocalStorage(LOCAL_PLY_KEY);
    if (localPly) {
      const searchIds = localPly.list.map(track => track.id);
      const updatePly = [];
      TrackInfoSearchListApi(searchIds).then((r) => {
        const searchTracks = r.data;
        searchTracks.map(search => {
          localPlyAddTrackInfo(search);
          const findInfo = localPly.list.filter(
              local => search.id === local.id);
          findInfo.map(info => {
            search.index = info.index;
            search.createdDateTime = info.createdDateTime;
            updatePly.push(createPlyInfo(search));
          })
        })
        localPly.list = updatePly;
        dispatch(localPlyActions.create(
            {userId: userInfo.userId, localPly: localPly}));
      }).catch((e) => {
        console.error(e);
      })
    } else {
      dispatch(localPlyActions.create({userId: userInfo.userId}));
    }
  }

  const getPlyTrackByOrder = (order) => {
    const id = localPly.item[localPly.playOrders[order]].id;
    const findTrack = localPlyTracks.tracks.filter(track => track.id === id);
    if (findTrack.length > 0) {
      return findTrack[0];
    }
    return undefined;
  }

  const changePlaying = (isPlaying) => {
    if (isPlaying) {
      bc.postMessage({type: "playing", key: playing.key});
    }
    dispatch(playingActions.changePlaying({isPlaying: isPlaying}));
  }

  const changePlyVisible = (isVisible) => {
    dispatch(localPlyTracksActions.changePlyVisible({isVisible: isVisible}));
  }


  // 현재 트랙정보 가져오기 재생 url x
  const createCurrentTrack = (order) => {
    updateSettings("played", 0);
    updateSettings("playedSeconds", 0);
    const data = getPlyTrackByOrder(order);
    dispatch(currentActions.create({info: data}))

  }
  const changeCurrTrackInfo = (order) => {
    const data = getPlyTrackByOrder(order);
    dispatch(currentActions.changeTrackInfo({info: data}))
  }
// order 정보 가져오기
  const createCurrentPlayLog = (order) => {
    const trackId = getPlyTrackByOrder(order).id
    TrackPlayApi(trackId).then((response) => {
        dispatch(currentActions.createPlayLog(
            {info: response.data, playLog: response.data.trackPlayLogRepDto}));
        changePlyTrackInfo(response.data);
    }).catch((error) => {
      if (error.status === HttpStatusCode.Forbidden || error.status
          === HttpStatusCode.NotFound) {
        localPlyCreate();
        // 현재 재생 할려던 트랙에 접근 권한이 없을 경우 -1 부여
        updateCurrTrackInfo("id", -1);
        toast.error(error.data?.errorDetails[0].message);
      }

    })
  }
  const updateCurrPlayLog = (key, value) => {
    dispatch(currentActions.updatePlayLog(
        {key: key, value: value}
    ));
  }
  const updateCurrTrackInfo = (key, value) => {
    dispatch(currentActions.updateTrackInfo(
        {key: key, value: value}
    ));
  }

  useEffect(() => {

  }, [playing, currentTrack])

  return {
    localPlyTracks,
    changeCurrTrackInfo,
    updatePlyTrackInfo,
    changePlayLog,
    localPlayLog,
    updateSettings,
    playerSettings,
    settingsCreate,
    changePlaying,
    playingClear,
    createCurrentTrack,
    createCurrentPlayLog,
    playing,
    localPlyAddTracks,
    shuffleOrders,
    currentTrack,
    getPlyTrackByOrder,
    updateCurrPlayLog,
    updateCurrTrackInfo,
    localPly,
    localPlyCreate,
    changePlyVisible
  };
};

export default useTrackPlayer;