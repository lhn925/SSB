import {
  USERS_INFO_USER_LOG
} from "utill/api/ApiEndpoints";
import {authApi} from "utill/api/interceptor/ApiAuthInterceptor";

export async function UserLogApi(type, offset, startDate, endDate) {
  try {
    const response = await authApi.get(USERS_INFO_USER_LOG + "?type=" +
        type + "&offset=" + offset + "&startDate=" + startDate + "&endDate="
        + endDate);
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


