import {toast} from "react-toastify";
import TrackLikeCancelApi from "utill/api/trackPlayer/TrackLikeCancelApi";
import TrackLikeApi from "utill/api/trackPlayer/TrackLikeApi";

export  function ToggleLike(trackInfo, updatePlyTrackInfo) {
  if (trackInfo.id === -1) {
    return;
  }

  const isLike = trackInfo.isLike;
  const title = trackInfo.title;
  const trackId = trackInfo.id;
  const toastText = !isLike? title +" was saved to your Library." : title +" cancel"

  handleLikeTracking(isLike, trackId)
  .then((r) => {

    console.log(r);
    if (r.status === 200) {
      toast.success("텍스트 추가) " + toastText);
    }

    updatePlyTrackInfo(trackId, "isLike", !isLike);
  }).catch((error) => {
    toast.error("텍스트 추가) 실패")
  })
}
async function handleLikeTracking(isLike, trackId) {
  if (isLike) {
    return TrackLikeCancelApi(trackId);
  } else {
    return TrackLikeApi(trackId);
  }
}
