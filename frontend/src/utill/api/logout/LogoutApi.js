import axios from "axios";
import {USERS_LOGOUT} from "utill/api/ApiEndpoints";

function LogoutApi(headers) {
  return axios.post(USERS_LOGOUT, null, {headers: headers});
}
export default LogoutApi;