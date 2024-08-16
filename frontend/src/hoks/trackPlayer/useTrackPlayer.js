import {useDispatch, useSelector} from "react-redux";
import {playingActions} from "store/trackplayer/playingReducer";
import TrackInfoApi from "utill/api/trackPlayer/TrackInfoApi";
import {currentActions} from "store/trackplayer/currentTrack";
import {toast} from "react-toastify";
import TrackPlayApi from "utill/api/trackPlayer/TrackPlayApi";
import {settingsActions} from "store/trackplayer/playerSettings";
import {localPlyActions} from "store/trackplayer/localPly";
import {playLogActions} from "store/trackplayer/localPlayLog";
import {HttpStatusCode} from "axios";
import {
  calculateOrder,
  createPlyInfo,
  loadFromLocalStorage,
  removeFromLocalStorage,
  shufflePlayOrder
} from "utill/function";
import {LOCAL_PLY_KEY} from "utill/enum/localKeyEnum";
import TrackInfoSearchListApi
  from "utill/api/trackPlayer/TrackInfoSearchListApi";
import {
  cachedTracksActions
} from "store/trackplayer/cachedTracks";
import {resetCurrentTrack, resetLocalPlyTrack} from "store/actions/index";
import {useTranslation} from "react-i18next";
import useCachedUsers from "hoks/cachedUsers/useCachedUsers";
import {
  MessageSender,
  useBroadcastChannel
} from "../../context/broadCast/useBroadcastChannel";
import useMyUserInfo from "../user/useMyUserInfo";
import {isAsyncThunkAction} from "@reduxjs/toolkit";
import useCachedTracks from "../cachedTracks/useCachedTracks";

const useTrackPlayer = () => {
  const dispatch = useDispatch();
  const {userReducer} = useMyUserInfo();
  const playing = useSelector(state => state?.playingReducer);
  const currentTrack = useSelector(state => state?.currentTrack);
  const playerSettings = useSelector(state => state?.playerSettings);
  const localPly = useSelector(state => state?.localPly);
  const localPlayLog = useSelector(state => state?.localPlayLog);
  const cachedTracks = useCachedTracks();
  const {t} = useTranslation();
  const bc = useBroadcastChannel();
  const {fetchUsers} = useCachedUsers();


  const playingClear = () => {
    dispatch(playingActions.clear());
  }
  const settingsCreate = () => {
    dispatch(settingsActions.create());
  }

  const changePlayLog = (order) => {
    const data = localPly.item[localPly.playOrders[order]];
    if (data === undefined) {
      return;
    }
    dispatch(playLogActions.changePlayLog(
        {
          id: data.id,
          index: data.index,
          startTime: new Date().getTime(),
          addDateTime: data.createdDateTime,
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

  const shuffleOrders = (shuffleArray) => {
    dispatch(localPlyActions.shuffleOrders(
        {playOrders: shuffleArray}
    ));
  }

  const localPlyAddTracks = async (trackId) => {

    const response = await cachedTracks.fetchTracks(trackId);
    try {
      const track = {...response[0]};
      track.userId = userReducer.userId;
      track.createdDateTime = new Date().getTime();
      track.playIndex = playerSettings.item.order;
      // localPlyAddTrackInfo(track);
      if (track) {
        dispatch(localPlyActions.addTracks(
            {data: track, text: t(`msg.player.local.limit`)}));
      }
      // const postUser = await fetchUsers(track.trackInfo.postUser.id);
      //
      // if (postUser.length > 0) {
      //   track.trackInfo.postUser = postUser[0];
      // }
    } catch (error ){
      console.error(error)
      toast.error(error.message);
    }
  }
  const localPlyAddTrackInfo = (data) => {

    cachedTracks.addTrack(data);
  }

  // 로컬 플레이리스트 정보 최신화
  const localPlyCreate = async () => {
    const localPly = loadFromLocalStorage(LOCAL_PLY_KEY);

    // userId 가 null일 경우
    if (userReducer.userId === null) {
      return;
    }
    const statusList = localPly?.list.filter(
        track => Number.parseInt(track.isStatus) === 1);
    if (statusList && statusList.length > 0) {
      const searchIds = statusList.map(item => item.id);
      const updatePly = [];
     await cachedTracks.fetchTracks(...searchIds).then(async (r) => {
        const searchTracks = r;
        const uIds = searchTracks.map(track => track.postUser.id);
        const users = await fetchUsers(uIds);
        if (users.length === 0) {
          removeFromLocalStorage(LOCAL_PLY_KEY);
          return;
        }
        const userMap = users.reduce((map, val) => {
          map.set(val.id, val);
          return map;
        }, new Map());

        searchTracks.map(search => {
          const trackInfo = search;
          trackInfo.postUser = userMap.get(trackInfo.postUser.id);
          // localPlyAddTrackInfo(search);
          const findInfo = statusList.filter(
              local => trackInfo.id === local.id);
          findInfo.map(info => {
            trackInfo.index = info.index;
            trackInfo.createdDateTime = info.createdDateTime;
            updatePly.push(createPlyInfo(search));
          })
        })
        localPly.list = updatePly;
        localPlyActionsCreate({userId: userReducer.userId, localPly: localPly})
      }).catch((e) => {
        console.error(e);
      })
    } else {
      localPlyActionsCreate({userId: userReducer.userId});
    }
  }
  const localPlyActionsCreate = (data) => {
    dispatch(localPlyActions.create(data));
  }
  const resetCurrTrack = () => {
    dispatch(resetCurrentTrack());
  }
  const resetPlyTrack = () => {
    dispatch(resetLocalPlyTrack());
  }
  const getStatusOnLocalPly = () => {
    const statusOnLocalPly = localPly.item.filter(
        (data) => data.isStatus === 1);
    if (statusOnLocalPly.length === 0) {
      resetPlyTrack();
      localPlyActionsCreate({userId: userReducer.userId});
    }
    return statusOnLocalPly;
  }
  const getPlyTrackByOrder = async (order, numberSign) => {
    const localPlyItem = calculateOrder(order, localPly.item,
        localPly.playOrders, getStatusOnLocalPly(), numberSign, updateSettings);
    if (localPlyItem) {
      const findTrack = await cachedTracks.fetchTracks(localPlyItem.id);
      // findTrack.index = localPlyItem.index;
      // findTrack.addDateTime = localPlyItem.createdDateTime;
      if (findTrack.length > 0) {
        const findTrackElement = {...findTrack[0]};
        findTrackElement.index = localPlyItem.index;
        findTrackElement.addDateTime = localPlyItem.createdDateTime;

        return findTrackElement;
      }
    }
    return undefined;
  }
  const getPlyTrackByTrackId = async (trackId) => {
    const findTrack = await cachedTracks.fetchTracks(trackId);
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

  const removePlyByTrackId = (trackId) => {

    dispatch(localPlyActions.removePlyByTrackId({id: trackId}));
  }
  const removePlyByIndex = (removeIndex) => {
    // const updateList = removeLocalPlyByIndex(removeIndex, localPly.item);
    dispatch(localPlyActions.removePlyByIndex({index: removeIndex}));
    // return updateList;
  }
  const changePlyVisible = (isVisible) => {
    cachedTracks.changePlyVisible(isVisible);
  }

  const changeOrder = (items) => {
    dispatch(localPlyActions.changeOrder(
        {
          items: items
        }))
  }

  // 현재 트랙정보 가져오기 재생 url x
  const createCurrentTrack = async (order, numberSign) => {
    updateSettings("played", 0);
    updateSettings("playedSeconds", 0);
    const data = await getPlyTrackByOrder(order, numberSign);
    if (data === undefined) {
      return;
    }
    dispatch(currentActions.create({info: data}))
  }
  const changeCurrTrackInfo = async (order, numberSign) => {
    const data = await getPlyTrackByOrder(order, numberSign);
    if (data === undefined) {
      return;
    }
    dispatch(currentActions.changeTrackInfo({info: data}))
  }
// order 정보 가져오기
  const createCurrentPlayLog = async (order, numberSign) => {
    const trackInfo = await getPlyTrackByOrder(order, numberSign);
    if (trackInfo === undefined) {
      return;
    }

    function removeTrackAndShuffleOrders(trackId,localPly,playerSettings) {
      removePlyByTrackId(trackId);
      const shuffleArray = shufflePlayOrder(localPly.playOrders,
          playerSettings.item.shuffle,
          localPly.item, playerSettings.item.order);
      shuffleOrders(shuffleArray);
      updateCurrTrackInfo("id", -1);
    }

    TrackPlayApi(trackInfo.id).then(async (response) => {

      const postUser = await fetchUsers(trackInfo.postUser.id);

      // 사용자가 검색되지 않는다면 예외발생
      if (postUser.length === 0) {
        removeTrackAndShuffleOrders(trackInfo.id,localPly,playerSettings);
        return;
      }
      const info = {
        ...response.data,
        addDateTime: trackInfo.addDateTime, // 재생목록에 추가한 시간
        index: trackInfo.index,
        postUser: postUser[0]
      };
      dispatch(currentActions.createPlayLog(
          {info: info, playLog: response.data.trackPlayLogRepDto}));
    }).catch((error) => {
      if (error.status === HttpStatusCode.Forbidden || error.status
          === HttpStatusCode.NotFound) {
        // 현재 재생 할려던 트랙에 접근 권한이 없을 경우 -1 부여
        removeTrackAndShuffleOrders(trackInfo.id,localPly,playerSettings);
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



  const addTrackAndPlay = (id) => {
    localPlyAddTracks(id);

  }

  const updatePlyTrackInfo = (trackId, key, value) => {
    cachedTracks.updatePlyTrackInfo(trackId,key,value)
  }


  return {
    removePlyByIndex,
    changeOrder,
    getPlyTrackByTrackId,
    cachedTracks,
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
    changePlyVisible,
    getStatusOnLocalPly,
    resetCurrTrack,
    localPlyActionsCreate,
    resetPlyTrack
  };
};

export default useTrackPlayer;