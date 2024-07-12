import {authApi} from "utill/api/interceptor/ApiAuthInterceptor";
import {USERS_PROFILE_LIST} from "utill/api/ApiEndpoints";

export default async function FetchUsersProfileApi(ids) {

  try {
    const response = await authApi.get(USERS_PROFILE_LIST,
        {params: {ids: ids}});

    return {
      code: response.status,
      data: response.data
    };
  } catch (error) {
    return {
      code: error.response.status,
      data: error.response.data
    };
  }

}