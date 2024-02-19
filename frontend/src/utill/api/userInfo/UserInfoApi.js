import axios from "axios";

/**
 * accessToken으로 유저정보 가져옴
 * @param headers
 * @returns {Promise<axios.AxiosResponse<any>>}
 * @constructor
 */
async function UserInfoApi(headers) {

  try {
    const response = await axios.get("./users/myInfo",{headers:headers});
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

export default UserInfoApi;