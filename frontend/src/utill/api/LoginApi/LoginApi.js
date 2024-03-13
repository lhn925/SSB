import axios from "axios";
import {LOGIN} from"utill/api/ApiEndpoints"
import {nonAuthApi} from "utill/api/interceptor/ApiAuthInterceptor";

function LoginApi(body){
  const headers = {
    "Content-Type": "application/json;charset=UTF-8"
  };
  return nonAuthApi.post(LOGIN, body);
}

export default LoginApi;