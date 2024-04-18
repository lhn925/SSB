import {useDispatch, useSelector} from "react-redux";
import {playingActions} from "store/play/playingReducer";
import TrackInfoApi from "utill/api/trackPlayer/TrackInfoApi";
import {currentActions} from "store/play/currentTrack";
import {toast} from "react-toastify";
import TrackPlayApi from "utill/api/trackPlayer/TrackPlayApi";
import {useEffect} from "react";
import {settingsActions} from "store/play/playerSettings";
import {localPlyActions} from "store/play/localPly";

const useTrackPlayer = () => {
  const dispatch = useDispatch();
  const playing = useSelector(state => state?.playingReducer);
  const currentTrack = useSelector(state => state?.currentTrack);
  const playerSettings = useSelector(state => state?.playerSettings);
  const localPly = useSelector(state => state?.localPly);

  const userInfo = useSelector(state => state?.userReducer);

  const playingClear = () => {
    dispatch(playingActions.clear());
  }
  const settingsCreate = () => {
    dispatch(settingsActions.create());
  }

  const updateSettings = (key,value) => {
    dispatch(settingsActions.updateSettings(
        {key:key,value:value}
    ));
  }

  const localPlyAddTracks = (trackId) => {
    console.log(trackId);
    TrackInfoApi(trackId).then((response) => {
      response.data.userId = userInfo.userId;
      response.data.createdDateTime = new Date().getTime();
      console.log(response.data);
      dispatch(localPlyActions.addTracks({data:response.data}));
    }).catch((error) => {
      toast.error(error.message);
    })
  }

  const localPlyCreate = () => {
      dispatch(localPlyActions.create({userId:userInfo.userId}));
  }



  const changePlaying = () => {
    dispatch(playingActions.changePlaying());
  }

  // 현재 트랙정보 가져오기 재생 url x
  const createCurrentTrack = (data) => {
      dispatch(currentActions.create({info: data}))
  }
// 현재 트랙정보 가져오기 재생 url O
  const updateCurrentTrack = (trackId) => {
    if (trackId === -1) {
      return;
    }
    TrackPlayApi(trackId).then((response) => {
      dispatch(currentActions.updatePlayLog(
          {info: response.data, playLog: response.data.trackPlayLogRepDto}));
    }).catch((error) => {
      toast.error(error.message);
    })
  }

  useEffect(() => {

  }, [playing, currentTrack])

  return {
    updateSettings,
    playerSettings,
    settingsCreate,
    changePlaying,
    playingClear,
    createCurrentTrack,
    updateCurrentTrack,
    playing,
    localPlyAddTracks,
    currentTrack,
    localPly,
    localPlyCreate
  };
};

export default useTrackPlayer;