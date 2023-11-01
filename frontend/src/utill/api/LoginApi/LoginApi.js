import axios from "axios";
import {authActions} from "../../../store/auth/authReducers";
import {toast} from "react-toastify";

function LoginApi(body){
  const headers = {
    "Content-Type": "application/json;charset=UTF-8"
  };
  return axios.post("./login", body, {headers:headers});
}

export default LoginApi;