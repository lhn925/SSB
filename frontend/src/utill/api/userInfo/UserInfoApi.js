import axios from "axios";

/**
 * accessToken으로 유저정보 가져옴
 * @param headers
 * @returns {Promise<{code, data}>}
 * @constructor
 */
// async function UserInfoApi(headers) {
//
//   try {
//     const response = await axios.get("/users/info",{headers:headers});
//     return {
//       code:response.status,
//       data:response.data
//     };
//   }catch (error) {
//     return {
//       code:error.response.status,
//       data:error.response.data
//     };
//   }
// }


async function UserInfoApi(headers) {

   let config = {url: "/users/info",headers:headers };
    axios.interceptors.request.use(config => {
      if(!config.headers) {
        return config;
      }
      if (config.url === "/login/refresh") {

      } else {

      }

      return config;
    })

}

export default UserInfoApi;