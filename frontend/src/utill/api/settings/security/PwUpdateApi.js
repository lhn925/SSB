import axios from "axios";
import {USERS_INFO_PW} from "../../ApiEndpoints";

export async function PwUpdateApi (body) {
  const headers = {
    "Content-Type": "application/json;charset=UTF-8"
  };
  try {
    const response = await axios.post(USERS_INFO_PW, body, {headers: headers})
    return {code: response.status, data: response.data};
  } catch (error) {
    return {code: error.response.status, data: error.response.data};
  }

}