import {authApi} from "../interceptor/ApiAuthInterceptor";
import {USERS_INFO_USERNAME} from "../ApiEndpoints";

export default async function UserNameUpdateApi(body) {
  try {
    const response = await authApi.post(USERS_INFO_USERNAME,body,{});
    return {
      data:response.data,
      code:response.status
    }
  } catch (e) {
    return {
      data:e.response.data,
      code:e.response.status
    }
  }

}