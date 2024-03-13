import axios from "axios";
import {USERS_JOIN} from "utill/api/ApiEndpoints"
import {nonAuthApi} from "utill/api/interceptor/ApiAuthInterceptor";

export async function JoinApi(body) {
  const dataObject = {code: null, data: null}
  try {
    const response = await nonAuthApi.post(USERS_JOIN, body)
    dataObject.code = response.status;
    dataObject.data = response.data;
  } catch (error) {
    dataObject.code = error.response.status;
    dataObject.data = error.response.data;
  } finally {
    return dataObject;
  }
}