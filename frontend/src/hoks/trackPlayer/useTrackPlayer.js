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
import {localPlyTracksActions} from "store/trackplayer/localPlyTracks";
import {resetCurrentTrack, resetLocalPlyTrack} from "store/actions/index";
import {useTranslation} from "react-i18next";
import useCachedUsers from "hoks/cachedUsers/useCachedUsers";

const useTrackPlayer = (bc, userReducer) => {
  const dispatch = useDispatch();
  const playing = useSelector(state => state?.playingReducer);
  const currentTrack = useSelector(state => state?.currentTrack);
  const playerSettings = useSelector(state => state?.playerSettings);
  const localPly = useSelector(state => state?.localPly);
  const localPlayLog = useSelector(state => state?.localPlayLog);
  const localPlyTracks = useSelector(state => state?.localPlyTracks);
  const {t} = useTranslation();
  const {addUsers, cachedUsers, fetchUsers, removeUser} = useCachedUsers();

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

  const shuffleOrders = (shuffleArray) => {
    dispatch(localPlyActions.shuffleOrders(
        {playOrders: shuffleArray}
    ));
  }

  const localPlyAddTracks = (trackId) => {
    TrackInfoApi(trackId).then(async (response) => {
      response.data.userId = userReducer.userId;
      response.data.createdDateTime = new Date().getTime();
      response.data.playIndex = playerSettings.item.order;
      const postUser = await fetchUsers(response.data.postUser.id);
      if (postUser.length > 0) {
        response.data.postUser = postUser[0];
        localPlyAddTrackInfo(response.data);
        dispatch(localPlyActions.addTracks(
            {data: response.data, text: t(`msg.player.local.limit`)}));
      }
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

    // userId 가 null일 경우
    if (userReducer.userId === null) {
      return;
    }
    const statusList = localPly?.list.filter(
        track => Number.parseInt(track.isStatus) === 1);
    if (statusList && statusList.length > 0) {
      const searchIds = statusList.map(item => item.id);
      const updatePly = [];
      TrackInfoSearchListApi(searchIds).then(async (r) => {
        const searchTracks = r.data;
        const uIds = searchTracks.map(track => track.postUser.id);

        const users = await fetchUsers(uIds);
        if (users.length === 0) {
          removeFromLocalStorage(LOCAL_PLY_KEY);
          return;
        }

        const userMap = await users.reduce((map, val) => {
          map.set(val.id, val);
          return map;
        }, new Map());

        searchTracks.map(search => {
          search.postUser = userMap.get(search.postUser.id);
          localPlyAddTrackInfo(search);
          const findInfo = statusList.filter(
              local => search.id === local.id);
          findInfo.map(info => {
            search.index = info.index;
            search.createdDateTime = info.createdDateTime;
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
  const getPlyTrackByOrder = (order, numberSign) => {
    const localPlyItem = calculateOrder(order, localPly.item,
        localPly.playOrders, getStatusOnLocalPly(), numberSign, updateSettings);
    if (localPlyItem) {
      const findTrack = localPlyTracks.tracks.filter(
          track => track.id === localPlyItem.id);
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
  const getPlyTrackByTrackId = (trackId) => {
    const findTrack = localPlyTracks.tracks.filter(
        track => track.id === trackId);
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
    dispatch(localPlyTracksActions.changePlyVisible({isVisible: isVisible}));
  }

  const changeOrder = (items) => {
    dispatch(localPlyActions.changeOrder(
        {
          items: items
        }))
  }

  // 현재 트랙정보 가져오기 재생 url x
  const createCurrentTrack = (order, numberSign) => {
    updateSettings("played", 0);
    updateSettings("playedSeconds", 0);
    const data = getPlyTrackByOrder(order, numberSign);
    if (data === undefined) {
      return;
    }
    dispatch(currentActions.create({info: data}))
  }
  const changeCurrTrackInfo = (order, numberSign) => {
    const data = getPlyTrackByOrder(order, numberSign);
    if (data === undefined) {
      return;
    }
    dispatch(currentActions.changeTrackInfo({info: data}))
  }
// order 정보 가져오기
  const createCurrentPlayLog = (order, numberSign) => {
    const trackInfo = getPlyTrackByOrder(order, numberSign);
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

  return {
    removePlyByIndex,
    changeOrder,
    getPlyTrackByTrackId,
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
    changePlyVisible,
    getStatusOnLocalPly,
    resetCurrTrack,
    localPlyActionsCreate,
    resetPlyTrack
  };
};

export default useTrackPlayer;