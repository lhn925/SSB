import axios from "axios";

function CaptchaApi(captchaKey,imageName) {
  if (captchaKey != "") {
    return axios.get("/Nkey/open/again?captchaKey=" + captchaKey + "&imageName=" + imageName);
  }
}

export default CaptchaApi;