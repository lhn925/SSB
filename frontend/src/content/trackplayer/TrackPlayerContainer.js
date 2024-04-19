import {TrackPlayer} from "content/trackplayer/TrackPlayer";
import {useEffect, useState} from "react";
import useTrackPlayer from "hoks/trackPlayer/useTrackPlayer";
import {createUploadActions} from "../../utill/function";

const TrackPlayerContainer = () => {
  // 재생 여부

  const {
    updateSettings,
    playerSettings,
    settingsCreate,
    changePlaying,
    playingClear,
    shuffleOrders,
    createCurrentTrack,
    updateCurrentTrack,
    playing,
    localPlyAddTracks,
    currentTrack,
    localPly,
    localPlyCreate
  } = useTrackPlayer();
  useEffect(() => {
    // settings 생성
    settingsCreate();
    // 현재 재생 목록 생성
    if (localPly.userId === null) {
      localPlyCreate();
    }


  }, [])

  useEffect(() => {
    if (localPly.item.length > 0) {
      createCurrentTrack(localPly.item[playerSettings.item.index]);
    }
  }, [localPly.item]);


  return (
      <TrackPlayer
          currentTrack={currentTrack}
          changePlaying={changePlaying}
          playing={playing}
          updateCurrentTrack={updateCurrentTrack}
          playerSettings={playerSettings}
          updateSettings={updateSettings}
          localPly={localPly}
          localPlyAddTracks={localPlyAddTracks}
          createCurrentTrack={createCurrentTrack}
          shuffleOrders={shuffleOrders}

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