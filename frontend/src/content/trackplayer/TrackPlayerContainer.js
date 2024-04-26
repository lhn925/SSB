import {TrackPlayer} from "content/trackplayer/TrackPlayer";
import {useEffect, useState} from "react";
import useTrackPlayer from "hoks/trackPlayer/useTrackPlayer";
import {createUploadActions, loadFromLocalStorage} from "utill/function";
import {LOCAL_PLAYER_SETTINGS} from "../../utill/enum/localKeyEnum";

const TrackPlayerContainer = ({bc}) => {
  // 재생 여부

  const {
    changePlayLog,
    localPlayLog,
    updateSettings,
    playerSettings,
    settingsCreate,
    updatePlyTrackInfo,
    changePlaying,
    playingClear,
    updateCurrPlayLog,
    shuffleOrders,
    createCurrentTrack,
    createCurrentPlayLog,
    playing,
    localPlyAddTracks,
    currentTrack,
    localPly,
    localPlyCreate,
    getPlyTrackByOrder,
    changeCurTrackInfo
  } = useTrackPlayer(bc);
  useEffect(() => {
    // settings 생성
    settingsCreate();
    // 현재 재생 목록 생성
    if (localPly.userId === null) {
      localPlyCreate();
    }
  }, [])

  useEffect(() => {
    function getLocalIndex() {
      const playLog = loadFromLocalStorage(localPlayLog.key);
      let findTrack;
      let findOrder = -1;
      if (playLog === undefined) {
        findTrack = localPly.item[0];
        findOrder = 0;
      }
      const localId = playLog[0];
      const localIndex = playLog[1];
      if (localId === null || localId === -1 || localIndex === null
          || localIndex === 0) {
        findTrack = localPly.item[0];
        findOrder = 0;
      }
      // 로컬에 있는 마지막 플레이리스트 로그 찾아서 반환
      localPly.item.map((data, index) => {
            if (data.index === localIndex) {
              findTrack = data;
              findOrder = index;
            }
          }
      );
      const order = playerSettings.item.order;
      // 만약 찾지 못하였으면
      console.log(order);
      console.log("findOrder : "+ findOrder);
      if (findOrder === -1) {
        const maxOrder = localPly.item.length;
        const isOver = order === maxOrder;

        // 만약 넘지 않았다
        if (!isOver) {
          findOrder = order;
          findTrack = localPly.playOrders[order + 1];
        } else {
          findTrack = localPly.item[0];
          findOrder = 0;
        }
      }

      return {findOrder, findTrack};
    }

    if (localPly.item.length > 0) {
      const {findTrack, findOrder} = getLocalIndex();
      updateSettings("order", findOrder);
      if (currentTrack.id === -1) {
        createCurrentTrack(findTrack);
      } else {
        changeCurTrackInfo(findTrack);
      }
      changePlayLog(findTrack.id, findTrack.index,
          new Date().getTime())
    }
  }, [localPly.item]);

  return (
      <TrackPlayer
          localPlayLog={localPlayLog}
          changePlayLog={changePlayLog}
          currentTrack={currentTrack}
          changePlaying={changePlaying}
          playing={playing}
          getPlyTrackByOrder={getPlyTrackByOrder}
          createCurrentPlayLog={createCurrentPlayLog}
          playerSettings={playerSettings}
          updateSettings={updateSettings}
          localPly={localPly}
          localPlyAddTracks={localPlyAddTracks}
          createCurrentTrack={createCurrentTrack}
          shuffleOrders={shuffleOrders}
          updateCurrPlayLog={updateCurrPlayLog}
          bc={bc}
          updatePlyTrackInfo={updatePlyTrackInfo}

          // player={player}
          // trackplayer={trackPlayer}
          // liked={currentUserLikes}
          // setPlayPause={handleSetPlayPause}
          // toggleLike={handleToggleLike}
          // fetchTrack={handleFetchTrack}
          // setTrackPlayer={handleSetTrackPlayer}
          // seekPlayer={handleSeekPlayer}
          // endCurrentTrack={handleEndCurrentTrack}
      />
  );
};

export default TrackPlayerContainer;