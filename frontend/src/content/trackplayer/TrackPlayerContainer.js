import {TrackPlayer} from "content/trackplayer/TrackPlayer";
import {useEffect, useState} from "react";
import useTrackPlayer from "hoks/trackPlayer/useTrackPlayer";
import {createUploadActions, loadFromLocalStorage} from "utill/function";
import {LOCAL_PLAYER_SETTINGS} from "../../utill/enum/localKeyEnum";

const TrackPlayerContainer = ({bc}) => {
  // 재생 여부

  const useTrackObject = useTrackPlayer(bc);
  useEffect(() => {
    // settings 생성
    useTrackObject.settingsCreate();
    // 현재 재생 목록 생성

  }, [])
  return (
      <TrackPlayer
          // updateCurrTrackInfo={updateCurrTrackInfo}
          // localPlyTracks={localPlyTracks}
          // changeCurrTrackInfo={changeCurrTrackInfo}
          // localPlayLog={localPlayLog}
          // changePlayLog={changePlayLog}
          // currentTrack={currentTrack}
          // changePlaying={changePlaying}
          // playing={playing}
          // getPlyTrackByOrder={getPlyTrackByOrder}
          // createCurrentPlayLog={createCurrentPlayLog}
          // playerSettings={playerSettings}
          // updateSettings={updateSettings}
          // localPly={localPly}
          // localPlyAddTracks={localPlyAddTracks}
          // createCurrentTrack={createCurrentTrack}
          // shuffleOrders={shuffleOrders}
          // updateCurrPlayLog={updateCurrPlayLog}
          // bc={bc}
          // updatePlyTrackInfo={updatePlyTrackInfo}
          // localPlyCreate={localPlyCreate}


          {...useTrackObject}
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