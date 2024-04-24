import axios, {HttpStatusCode} from "axios";
import {persistor, store} from "store/store";
import {authActions} from "store/auth/authReducers";
import mem from "mem";
import {LOGIN_REFRESH, USERS_INFO} from "utill/api/ApiEndpoints";
import {toast} from "react-toastify";
import {useTranslation} from "react-i18next";
import {removeFromLocalStorage} from "utill/function";
import {LOCAL_PLY_KEY} from "utill/enum/localKeyEnum";
// 토큰이 불 필요한 URL
export const nonAuthApi = axios.create({
  baseURL: `${process.env.REACT_APP_API_URL}`,
  headers: {
    "Content-Type": "application/json;charset=UTF-8",
  }
});
export const nonGetAuthApi = axios.create({
  baseURL: `${process.env.REACT_APP_API_URL}`,
});

// 토큰이 필요한
export const authApi = axios.create({
  baseURL: `${process.env.REACT_APP_API_URL}`,
});

authApi.interceptors.request.use((config) => {
  const state = store.getState().authReducer;
  const access = state.access;

  if (access) {
    config.headers.Authorization = access;
  }
  return config;
}, (error) => {
  return Promise.reject(error);
})

export async function postRefreshToken(refreshToken) {
  return axios.post(LOGIN_REFRESH, {}, {
    headers: {
      Authorization: refreshToken
    }
  });
}

/**
 *  메모이제이션은 비용이 많이 드는 연산의 결과를 저장해두었다가 동일한 입력값에 대해선 저장된 결과를 바로 반환함으로써 성능을 향상시킬 수 있습니다
 */
// refresh 중복 요청 방지를 위한 mem(memoization) library 사용
const memoizedPostRefreshToken = mem(postRefreshToken, {maxAge: 1000});

authApi.interceptors.response.use(
    // 200 이 나올때 처리
    (response) => {
      return response;
    }, async (error) => {
      const {
        config, response: {status},
      } = error;

      // const {t} = useTranslation();
      // 토큰이 만료되었을때
      if (status === HttpStatusCode.Unauthorized) {
        const state = store.getState().authReducer;
        const refresh = state.refresh;
        const originRequest = config;
        try {
          const response = await memoizedPostRefreshToken(refresh);
          const newAccessToken = response.data;
          // accessToken 저장
          store.dispatch(authActions.setAccess(newAccessToken));
          store.dispatch(authActions.setAccessHeader());
          originRequest.headers.Authorization = newAccessToken;
          // 재요청
          return axios(originRequest);
        } catch (error) {
          removeFromLocalStorage(LOCAL_PLY_KEY);
          await persistor.purge();
          window.location.href = "/";
          // toast.error(t(`errorMsg.error.token`));
          return error;
        }
      } else if (status === HttpStatusCode.InternalServerError) {
        // window.location.href = "/500";
      } else if (status === HttpStatusCode.NotFound) {
        if (config.url === USERS_INFO) {
          return error.response;
        } else {
          window.location.href = "/404";
        }
      } else {
        return error.response;
      }
    }
)

export const authTrackApi = axios.create({
  baseURL: `${process.env.REACT_APP_API_URL}`,
});

authTrackApi.interceptors.request.use((config) => {
  const state = store.getState().authReducer;
  const access = state.access;
  if (access) {
    config.headers.Authorization = access;
  }
  return config;
}, (error) => {
  return Promise.reject(error);
})

authTrackApi.interceptors.response.use(
    // 200 이 나올때 처리
    (response) => {
      return response;
    }, async (error) => {
      if (!error.response) {
        return Promise.reject('Network error or no response from server');
      }
        const {
          config, response: {status},
        } = error;
        // const {t} = useTranslation();
        // 토큰이 만료되었을때
        if (status === HttpStatusCode.Unauthorized) {
          const state = store.getState().authReducer;
          const refresh = state.refresh;
          const originRequest = config;
          try {
            const response = await memoizedPostRefreshToken(refresh);
            const newAccessToken = response.data;
            // accessToken 저장
            store.dispatch(authActions.setAccess(newAccessToken));
            store.dispatch(authActions.setAccessHeader());
            originRequest.headers.Authorization = newAccessToken;
            // 재요청
            return axios(originRequest);
          } catch (error) {
            await persistor.purge();
            return Promise.reject(error);
          }
        } else {
          return Promise.reject(error);
        }

    }
)
