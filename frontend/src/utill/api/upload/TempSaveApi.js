import {
  TRACKS_TEMP_FILE_SAVE
} from "utill/api/ApiEndpoints";
import {authApi} from "utill/api/interceptor/ApiAuthInterceptor";


export async function TempSaveApi(setUploadPercent,tempToken,formData) {
  try {
    const response = await authApi.post
    (TRACKS_TEMP_FILE_SAVE,formData,{
      onUploadProgress:(progressEvent) => {
        let percentage = (progressEvent.loaded * 100) / progressEvent.total;
        //progressEvent.loaded  현재까지 로드 된 수치
        // progressEvent.total 전체 수치

        let percentCompleted = Math.round(percentage);
        setUploadPercent(tempToken,percentCompleted);
        // commit('setUploadPercent', percentCompleted);
        console.log( "percentCompleted : "+ percentCompleted)
        // onUploadProgress 는 갱신될 때마다 커밋을 수행한다
      },
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
    return {
      code:response.status,
      data:response.data
    };
  } catch (error) {
    return {
      code:error.response.status,
      data:error.response.data
    };
  }
}

