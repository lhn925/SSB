import {TrackPlayer} from "content/trackplayer/TrackPlayer";
import {useEffect, useState} from "react";
import useTrackPlayer from "hoks/trackPlayer/useTrackPlayer";
import {createUploadActions, loadFromLocalStorage} from "utill/function";

const TrackPlayerContainer = ({bc}) => {
  // 재생 여부

  const {
    changePlayLog,
    localPlayLog,
    updateSettings,
    playerSettings,
    settingsCreate,
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
      let findTrack = localPly.item[0];
      let findOrder = 0;
      if (playLog === undefined) {
        return {findOrder, findTrack};
      }
      const localId = playLog[0];
      const localIndex = playLog[1];
      if (localId === null || localId === -1 || localIndex === null
          || localIndex === 0) {
        return {findOrder, findTrack};
      }
      // 로컬에 있는 마지막 플레이리스트 로그 찾아서 반환
      localPly.item.filter((data, index) => {
            if (data.index === localIndex) {
              findTrack = data;
              findOrder = index;
            }
          }
      );
      return {findOrder, findTrack};
    }

    if (localPly.item.length > 0) {
      const {findTrack, findOrder} = getLocalIndex();
      updateSettings("order", findOrder);
      createCurrentTrack(findTrack);
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