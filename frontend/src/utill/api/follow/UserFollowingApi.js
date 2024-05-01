import {authApi, authTrackApi} from "utill/api/interceptor/ApiAuthInterceptor";
import {
  TRACKS_LIKES,
  USERS_ACTION_MY_FOLLOWING,
  USERS_INFO_BLOCKED
} from "utill/api/ApiEndpoints";

export async function UserFollowingApi(followingId) {
  return await authApi.post(USERS_ACTION_MY_FOLLOWING + followingId);
  try {
    const response = await authApi.post(USERS_ACTION_MY_FOLLOWING + followingId);
    return {
      code:response.status,
      data:response.data
    };
  }catch (error) {
    return {
      code:error.response.status,
      data:error.response.data
    };
  }
}