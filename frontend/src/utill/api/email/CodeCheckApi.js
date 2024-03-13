import {EMAIL_CODE_CHECK} from "utill/api/ApiEndpoints";
import {nonAuthApi} from "utill/api/interceptor/ApiAuthInterceptor";

export async function CodeCheckApi(body) {

  const dataObject = {code: null, data: null}
  try {
    const response = await nonAuthApi.post(EMAIL_CODE_CHECK, body)
    dataObject.code = response.status;
    dataObject.data = response.data;
  } catch (error) {
    dataObject.code = error.response.status;
    dataObject.data = error.response.data;
  } finally {
    return dataObject;
  }
}