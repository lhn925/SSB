import {TrackPlayer} from "content/trackplayer/TrackPlayer";
import {useEffect} from "react";
import useTrackPlayer from "hoks/trackPlayer/useTrackPlayer";

const TrackPlayerContainer = ({bc}) => {
  // 재생 여부

  const useTrackObject = useTrackPlayer(bc);
  useEffect(() => {
    // settings 생성
    useTrackObject.settingsCreate();
    // 현재 재생 목록 생성
    if (useTrackObject.localPly.userId === null) {
      useTrackObject.localPlyCreate();
    }

  }, [])
  return (
      <TrackPlayer
          {...useTrackObject}
      />
  );
};

export default TrackPlayerContainer;