import {toast} from "react-toastify";
import TrackLikeCancelApi from "utill/api/trackPlayer/TrackLikeCancelApi";
import TrackLikeApi from "utill/api/trackPlayer/TrackLikeApi";
import {HttpStatusCode} from "axios";

export function ToggleLike(trackId,title,isLike, updatePlyTrackInfo,t) {
  if (trackId === -1 || trackId === undefined) {
    return;
  }

  const toastText = !isLike ? `msg.like.track.save` : `msg.like.track.cancel`;

  handleLikeTracking(isLike, trackId)
  .then((r) => {
    toast.success(t(toastText,{title:title}));
    updatePlyTrackInfo(trackId, "isLike", !isLike);
  }).catch((error) => {
    if (error.status === HttpStatusCode.BadRequest) {
      updatePlyTrackInfo(trackId, "isLike", !isLike);
    }
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
