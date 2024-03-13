import {USERS_HELP_SHOW} from "utill/api/ApiEndpoints";
import {nonGetAuthApi} from "utill/api/interceptor/ApiAuthInterceptor";

export async function FindIdApi(email, authToken) {
  try {
    const response = await nonGetAuthApi.get(USERS_HELP_SHOW+"?authToken="+authToken+"&email="+email)

    return {code: response.status, data: response.data};
  } catch (error) {
    return {code: error.response.status, data: error.response.data};
  }

}