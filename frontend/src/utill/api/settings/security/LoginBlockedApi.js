import {
  USERS_INFO_BLOCKED,
  USERS_INFO_LOGIN_DEVICE
} from "utill/api/ApiEndpoints";
import {authApi} from "utill/api/interceptor/ApiAuthInterceptor";


export async function LoginBlockedApi(body) {
  try {
    const response = await authApi.post(USERS_INFO_BLOCKED,body);
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


