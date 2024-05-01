import {authApi} from "utill/api/interceptor/ApiAuthInterceptor";
import {
  USERS_ACTION_MY_FOLLOWING,
} from "utill/api/ApiEndpoints";

export async function UserUnFollowingApi(followingId) {
  try {
    const response = await authApi.delete(USERS_ACTION_MY_FOLLOWING + followingId);
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