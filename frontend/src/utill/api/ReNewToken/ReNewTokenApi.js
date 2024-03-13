import axios from "axios";
import {LOGIN_REFRESH} from "../ApiEndpoints";

async function ReNewTokenApi (headers) {
  try {
    const response = await axios.post(LOGIN_REFRESH, {}, {headers: headers});
    return {
      code:response.status,
      data:response.data
    };
  }catch (error) {
    return {
      code:error.response.status,
      data:error.response.data
    };
  }
}

export default ReNewTokenApi;