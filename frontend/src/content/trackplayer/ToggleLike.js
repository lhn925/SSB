import {toast} from "react-toastify";
import TrackLikeCancelApi from "utill/api/trackPlayer/TrackLikeCancelApi";
import TrackLikeApi from "utill/api/trackPlayer/TrackLikeApi";
import {TRACK_LIKED_IDS} from "store/userInfo/userReducers";

export function ToggleLike(trackId, title, userReducer, updatePlyTrackInfo, t) {
  if (trackId === -1 || trackId === undefined) {
    return;
  }


  const isLike = userReducer.isTrackLike(trackId);
  const toastText = !isLike ? `msg.like.track.save` : `msg.like.track.cancel`;
  // updatePlyTrackInfo(trackId, "isLike", !isLike);

  if (!isLike) {
    userReducer.addArrayValueByType(trackId, TRACK_LIKED_IDS);
  } else {
    userReducer.removeArrayValueByType(trackId, TRACK_LIKED_IDS);
  }
  handleLikeTracking(isLike, trackId).then((r) => {
    toast.success(t(toastText, {title: title}));
  }).catch((error) => {
    toast.error(error.data?.errorDetails[0].message);
  })
}

function handleLikeTracking(isLike, trackId) {
  if (isLike) {
    return TrackLikeCancelApi(trackId);
  } else {
    return TrackLikeApi(trackId);
  }
}
