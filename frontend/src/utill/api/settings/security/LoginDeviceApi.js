import {USERS_INFO_LOGIN_DEVICE} from "utill/api/ApiEndpoints";
import {authApi} from "utill/api/interceptor/ApiAuthInterceptor";


export async function LoginDeviceApi(offset) {
  try {
    const response = await authApi.get(USERS_INFO_LOGIN_DEVICE +"?offset="+offset);
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


