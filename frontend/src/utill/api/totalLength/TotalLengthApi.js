import {
  TRACKS_INFO_TOTAL
} from "utill/api/ApiEndpoints";
import {authApi} from "utill/api/interceptor/ApiAuthInterceptor";

export async function TotalLengthApi() {
  try {
    const response = await authApi.get(TRACKS_INFO_TOTAL);
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


