import axios from "axios";

export async function QueryIdApi(userId) {
  try {
    const response = await axios.get("./user/help/idQuery?userId="+userId)

    return {code: response.status, data: response.data};
  } catch (error) {
    return {code: error.response.status, data: error.response.data};
  }

}