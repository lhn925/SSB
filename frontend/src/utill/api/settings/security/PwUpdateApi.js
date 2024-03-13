import {USERS_INFO_PW} from "utill/api/ApiEndpoints";
import {authApi} from "utill/api/interceptor/ApiAuthInterceptor";

export async function PwUpdateApi (body) {
  try {
    const response = await authApi.post(USERS_INFO_PW, body)
    return {code: response.status, data: response.data};
  } catch (error) {
    return {code: error.response.status, data: error.response.data};
  }

}