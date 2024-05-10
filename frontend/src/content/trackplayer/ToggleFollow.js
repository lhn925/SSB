import {toast} from "react-toastify";
import TrackLikeCancelApi from "utill/api/trackPlayer/TrackLikeCancelApi";
import TrackLikeApi from "utill/api/trackPlayer/TrackLikeApi";
import {UserUnFollowingApi} from "utill/api/follow/UserUnFollowingApi";
import {UserFollowingApi} from "utill/api/follow/UserFollowingApi";
import {HttpStatusCode} from "axios";
import {useTranslation} from "react-i18next";

export function ToggleFollow(trackId, postUser, updatePlyTrackInfo,t) {

  if (trackId === -1 || postUser.id === -1) {
    return;
  }

  const isFollow = postUser.isFollow;
  const userName = postUser.userName;
  const uid = postUser.id;

  const toastText = !isFollow ? `msg.follow.save` : `msg.follow.cancel`;

  handleFollowUser(isFollow, uid)
  .then((r) => {
    toast.success(t(toastText,{userName:userName}));
    updatePlyTrackInfo(trackId, "postUser", {...postUser,isFollow:!isFollow});
  }).catch((error) => {
    if (error.status === HttpStatusCode.BadRequest) {
      updatePlyTrackInfo(trackId, "isFollow", !isFollow);
    }
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
