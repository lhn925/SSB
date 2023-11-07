import axios from "axios";

export async function ResetApi (body) {
  const headers = {
    "Content-Type": "application/json;charset=UTF-8"
  };
  try {
    const response = await axios.post("./user/help/reset", body, {headers: headers})
    return {code: response.status, data: response.data};
  } catch (error) {
    return {code: error.response.status, data: error.response.data};
  }

}