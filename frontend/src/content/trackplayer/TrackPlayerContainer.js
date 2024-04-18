import {TrackPlayer} from "content/trackplayer/TrackPlayer";
import {useEffect, useState} from "react";
import useTrackPlayer from "hoks/trackPlayer/useTrackPlayer";

const TrackPlayerContainer = () => {
  // 재생 여부

  const {
    changePlaying,
    createCurrentTrack,
    updateCurrentTrack,
    playing,
    currentTrack,
    playerSettings,
    updateSettings,
    settingsCreate,
  } = useTrackPlayer();
  useEffect(() => {
    // settings 생성
    settingsCreate();
    createCurrentTrack(1);
  }, [])
  return (
      <TrackPlayer
          currentTrack={currentTrack}
          changePlaying={changePlaying}
          playing={playing}
          updateCurrentTrack={updateCurrentTrack}
          playerSettings={playerSettings}
          updateSettings={updateSettings}

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