import {authApi} from "../interceptor/ApiAuthInterceptor";
import {USERS_INFO_PICTURE} from "../ApiEndpoints";

export default async function PictureDeleteApi(formData) {
  try {
    const response = await authApi.delete(USERS_INFO_PICTURE);
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