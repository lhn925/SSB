import {
  USERS_INFO_LOGOUT_STATUS
} from "utill/api/ApiEndpoints";
import {authApi} from "utill/api/interceptor/ApiAuthInterceptor";


export async function LogOutStatusApi(body) {
  try {
    const response = await authApi.patch
    (USERS_INFO_LOGOUT_STATUS,body);
    return {
      code:response.status,
      data:response.data
    };
  } catch (error) {
    return {
      code:error.response.status,
      data:error.response.data
    };
  }
}


