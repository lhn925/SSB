import {toast} from "react-toastify";
import TrackLikeCancelApi from "utill/api/trackPlayer/TrackLikeCancelApi";
import TrackLikeApi from "utill/api/trackPlayer/TrackLikeApi";
import {UserUnFollowingApi} from "utill/api/follow/UserUnFollowingApi";
import {UserFollowingApi} from "utill/api/follow/UserFollowingApi";
import {HttpStatusCode} from "axios";

export function ToggleFollow(trackId, postUser, updatePlyTrackInfo) {
  if (trackId === -1 || postUser.id === -1) {
    return;
  }

  const isFollow = postUser.isFollow;
  const userName = postUser.userName;
  const uid = postUser.id;
  const toastText = !isFollow ? userName + " was saved to your Library." : userName
      + " cancel"

  handleFollowUser(isFollow, uid)
  .then((r) => {
    toast.success("텍스트 추가) " + toastText);
    updatePlyTrackInfo(trackId, "postUser", {...postUser,isFollow:!isFollow});
  }).catch((error) => {
    if (error.status === HttpStatusCode.BadRequest) {
      updatePlyTrackInfo(trackId, "isFollow", !isFollow);
    }
    // toast.error("텍스트 추가) 실패")
    toast.error(error.data?.errorDetails[0].message);
  })
}

function handleFollowUser(isFollow, id) {
  if (isFollow) {
    return UserUnFollowingApi(id);
  } else {
    return UserFollowingApi(id);
  }
}
