import {
  TRACKS_SAVE,
  TRACKS_TEMP_FILE_SAVE
} from "utill/api/ApiEndpoints";
import {authApi} from "utill/api/interceptor/ApiAuthInterceptor";
import axios from "axios";

export async function TrackSaveApi(body) {

  // Abort Controller 는 웹 요청 취소를 할 수 있게 해주는 API이다.
  // axios 에서도 CancelToken 대신 AbortController를 권장한다고 한다.

  try {
    const response = await authApi.post
    (TRACKS_SAVE, body, {
      "Content-Type": `multipart/form-data`
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

