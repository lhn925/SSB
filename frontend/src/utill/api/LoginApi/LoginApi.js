import {LOGIN} from"utill/api/ApiEndpoints"
import {nonAuthApi} from "utill/api/interceptor/ApiAuthInterceptor";

function LoginApi(body){
  return nonAuthApi.post(LOGIN, body);
}

export default LoginApi;