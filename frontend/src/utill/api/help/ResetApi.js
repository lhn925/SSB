import axios from "axios";
import {nonAuthApi} from "utill/api/interceptor/ApiAuthInterceptor";
import {USERS_HELP_RESET} from "utill/api/ApiEndpoints";

export async function ResetApi (body) {
  const headers = {
    "Content-Type": "application/json;charset=UTF-8"
  };
  try {
    const response = await nonAuthApi.post(USERS_HELP_RESET, body, {headers: headers})
    return {code: response.status, data: response.data};
  } catch (error) {
    return {code: error.response.status, data: error.response.data};
  }

}