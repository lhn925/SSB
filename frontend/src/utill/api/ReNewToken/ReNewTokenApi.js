import axios from "axios";

function ReNewTokenApi (headers) {
  return axios.post("/login/refresh",{headers});
}

export default ReNewTokenApi;