import {toast} from "react-toastify";
import TrackLikeCancelApi from "utill/api/trackPlayer/TrackLikeCancelApi";
import TrackLikeApi from "utill/api/trackPlayer/TrackLikeApi";
import {HttpStatusCode} from "axios";

export function ToggleLike(trackId,title,isLike, updatePlyTrackInfo) {
  if (trackId === -1) {
    return;
  }

  const toastText = !isLike ? title + " was saved to your Library." : title
      + " cancel"

  handleLikeTracking(isLike, trackId)
  .then((r) => {
    toast.success("텍스트 추가) " + toastText);
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
