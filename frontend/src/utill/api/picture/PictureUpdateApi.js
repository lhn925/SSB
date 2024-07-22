import {authApi} from "../interceptor/ApiAuthInterceptor";
import {USERS_INFO_PICTURE} from "../ApiEndpoints";

export default async function PictureUpdateApi(formData) {
  try {
    const response = await authApi.post(USERS_INFO_PICTURE,formData,{});
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