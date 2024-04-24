import {useDispatch, useSelector} from "react-redux";
import {playingActions} from "store/play/playingReducer";
import TrackInfoApi from "utill/api/trackPlayer/TrackInfoApi";
import {currentActions} from "store/play/currentTrack";
import {toast} from "react-toastify";
import TrackPlayApi from "utill/api/trackPlayer/TrackPlayApi";
import {useEffect} from "react";
import {settingsActions} from "store/play/playerSettings";
import {localPlyActions} from "store/play/localPly";
import {playLogActions} from "../../store/play/localPlayLog";

const useTrackPlayer = (bc) => {
  const dispatch = useDispatch();
  const playing = useSelector(state => state?.playingReducer);
  const currentTrack = useSelector(state => state?.currentTrack);
  const playerSettings = useSelector(state => state?.playerSettings);
  const localPly = useSelector(state => state?.localPly);
  const localPlayLog = useSelector(state => state?.localPlayLog);

  const userInfo = useSelector(state => state?.userReducer);

  const playingClear = () => {
    dispatch(playingActions.clear());
  }
  const settingsCreate = () => {
    dispatch(settingsActions.create());
  }

  const changePlayLog = (id, index, startTime) => {
    dispatch(playLogActions.changePlayLog(
        {
          id:id,
          index:index,
          startTime:startTime
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
      // console.log(response.data);
      dispatch(localPlyActions.addTracks({data: response.data}));
    }).catch((error) => {
      toast.error(error.message);
    })
  }

  const localPlyCreate = () => {
    dispatch(localPlyActions.create({userId: userInfo.userId}));
  }

  const getPlyTrackByOrder = (order) => {
    return localPly.item[localPly.playOrders[order]];
  }

  const changePlaying = (isPlaying) => {
    if (isPlaying) {
      bc.postMessage({type: "playing", key: playing.key});
    }
    dispatch(playingActions.changePlaying({isPlaying: isPlaying}));
  }

  // 현재 트랙정보 가져오기 재생 url x
  const createCurrentTrack = (data) => {
    updateSettings("played", 0);
    updateSettings("playedSeconds", 0);
    dispatch(currentActions.create({info: data}))

  }
// 현재 트랙정보 가져오기 재생 url O
  const createCurrentPlayLog = (trackId) => {
    if (trackId === -1) {
      return;
    }
    TrackPlayApi(trackId).then((response) => {
      dispatch(currentActions.createPlayLog(
          {info: response.data, playLog: response.data.trackPlayLogRepDto}));
    }).catch((error) => {
      toast.error(error.data?.errorDetails[0].message);
    })
  }
  const updateCurrPlayLog = (key, value) => {
    dispatch(currentActions.updatePlayLog(
        {key: key, value: value}
    ));
  }

  useEffect(() => {

  }, [playing, currentTrack])

  return {
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
    localPly,
    localPlyCreate
  };
};

export default useTrackPlayer;