import {API_LOGIN} from"utill/api/ApiEndpoints"
import {nonAuthApi} from "utill/api/interceptor/ApiAuthInterceptor";

function LoginApi(body){
  return nonAuthApi.post(API_LOGIN, body);
}

export default LoginApi;