import {
  TRACKS_TEMP_FILE_SAVE
} from "utill/api/ApiEndpoints";
import {authApi} from "utill/api/interceptor/ApiAuthInterceptor";
import axios from "axios";

export async function TempSaveApi(setTracksUploadPercent, tempToken, formData) {

  // Abort Controller 는 웹 요청 취소를 할 수 있게 해주는 API이다.
  // axios 에서도 CancelToken 대신 AbortController를 권장한다고 한다.
  const abortController = new AbortController();

  try {
    const response = await authApi.post
    (TRACKS_TEMP_FILE_SAVE, formData, {
      onUploadProgress: (progressEvent) => {
        let percentage = (progressEvent.loaded * 100) / progressEvent.total;
        //progressEvent.loaded  현재까지 로드 된 수치
        // progressEvent.total 전체 수치

        let percentCompleted = Math.round(percentage);
        setTracksUploadPercent(tempToken, percentCompleted, abortController);
        // commit('setUploadPercent', percentCompleted);
        // onUploadProgress 는 갱신될 때마다 커밋을 수행한다
      },
      signal: abortController.signal,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
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

