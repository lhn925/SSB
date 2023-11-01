import axios from "axios";

async function ReNewTokenApi (headers) {
  try {
    const response = await axios.post("./login/refresh", {}, {headers: headers});
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