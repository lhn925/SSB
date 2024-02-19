import axios from "axios";

function LogoutApi(headers){
  return axios.post("./users/logout", null, {headers:headers});
}

export default LogoutApi;