import axios from "axios";
import {nonGetAuthApi} from "utill/api/interceptor/ApiAuthInterceptor";
import {USERS_HELP_IDQUERY} from "utill/api/ApiEndpoints";

export async function QueryIdApi(userId) {
  try {
    const response = await nonGetAuthApi.get(USERS_HELP_IDQUERY +"?userId="+userId)

    return {code: response.status, data: response.data};
  } catch (error) {
    return {code: error.response.status, data: error.response.data};
  }

}