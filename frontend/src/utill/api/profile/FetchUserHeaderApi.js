import {
  USERS_PROFILE
} from "utill/api/ApiEndpoints";
import {authApi} from "utill/api/interceptor/ApiAuthInterceptor";

async function FetchUserHeaderApi(userName) {
  try {
    const response = await authApi.get(USERS_PROFILE + userName);
    return {code: response.status, data: response.data};
  } catch (error) {
    return {code: error.response.status, data: error.response.data};
  }
}

export default FetchUserHeaderApi