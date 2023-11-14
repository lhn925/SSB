import axios from "axios";

function LogoutApi(headers){
  return axios.post("./user/logout", null, {headers:headers});
}

export default LogoutApi;