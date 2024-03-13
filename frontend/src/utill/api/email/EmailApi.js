import {nonAuthApi} from "utill/api/interceptor/ApiAuthInterceptor";

export async function EmailApi(url, body) {
  const dataObject = {code: null, data: null}
  try {
    const response = await nonAuthApi.post(url, body)
    dataObject.code = response.status;
    dataObject.data = response.data;
  } catch (error) {
    dataObject.code = error.response.status;
    dataObject.data = error.response.data;
  } finally {
    return dataObject;
  }
}