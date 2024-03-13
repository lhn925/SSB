import axios from "axios";
import {NKEY_OPEN_AGAIN, NKEY_OPEN_CAPTCHA} from "utill/api/ApiEndpoints";
import {nonAuthApi} from "utill/api/interceptor/ApiAuthInterceptor";

function CaptchaApi(captchaKey,imageName) {
  if (captchaKey !== null && imageName !== null) {
    return nonAuthApi.get(NKEY_OPEN_AGAIN+"?captchaKey=" + captchaKey + "&imageName=" + imageName);
  } else {
    return nonAuthApi.get(NKEY_OPEN_CAPTCHA);
  }
}
export default CaptchaApi;