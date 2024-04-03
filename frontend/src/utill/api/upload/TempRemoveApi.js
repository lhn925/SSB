import {
  TRACKS_TEMP_FILE_SAVE_LIST
} from "utill/api/ApiEndpoints";
import {authApi} from "utill/api/interceptor/ApiAuthInterceptor";

export async function TempRemoveApi(body) {
  try {
    const response = await authApi.delete
    (TRACKS_TEMP_FILE_SAVE_LIST, {
      data:body
    });
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

