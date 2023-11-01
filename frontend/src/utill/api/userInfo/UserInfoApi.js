import axios from "axios";

function UserInfoApi(captchaKey,imageName) {
    return axios.get("/Nkey/open/again?captchaKey=" + captchaKey + "&imageName=" + imageName);
  }
}

export default UserInfoApi;