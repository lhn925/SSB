import axios from "axios";
import {nonGetAuthApi} from "utill/api/interceptor/ApiAuthInterceptor";
import {USERS_HELP_RESET} from "utill/api/ApiEndpoints";

export default async function ResetFormApi(userId, authToken, email) {
  try {
    const response = await nonGetAuthApi.get(
        USERS_HELP_RESET+"?userId=" + userId + "&authToken=" + authToken
        + "&email=" + email);

    return {code: response.status, data: response.data};
  } catch (error) {
    return {code: error.response.status, data: error.response.data};
  }
}