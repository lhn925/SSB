function Login() {
  this.$userId = null;
  this.$password = null;
  this.$url = null;

  this.$captchaKey = null;
  this.$imagePath = null;

  // 2차인증 코드
  this.$captcha = null;
  this._init();
}

Login.prototype._init = function () {
  this.$userId = _getElementById("userId");
  this.$password = _getElementById("password");
  this.$url = _getElementById("url");
  this.$captchaKey = _getElementById("captchaKey");
  this.$imagePath = _getElementById("imagePath");
  this.$captcha = _getElementById("captcha");

  this._LoginSubBtnClickAddEvent();
  this._captchaBtnClickAddEvent();
}

// 로그인이시 체크 함수
Login.prototype._LoginSubBtnClickAddEvent = function () {

  const $loginSubBtn = _getElementById("loginSubBtn");

  let isClicking = false;
  $loginSubBtn.onclick = function () {
    if (isClicking) {
      return;
    }
    isClicking = true;
    let userIdVal = _login.$userId.value.split(" ").join("");
    let pwVal = _login.$password.value.split(" ").join("");
    let capKeyVal = _login.$captchaKey.value.split(" ").join("");

    if (userIdVal == "") {
      _removeByClass("login-err", "display-none");
      _innerTextByClass("login-err", messages["userId.NotBlank"])
      isClicking = false;
      return;
    }
    if (pwVal == "") {
      _removeByClass("login-err", "display-none");
      _innerTextByClass("login-err", messages["password.NotBlank"]);
      isClicking = false;
      return;
    }

    if (capKeyVal != "") {
      let captchaVal = _login.$captcha.value.split(" ").join("");
      if (captchaVal == "") {
        _removeByClass("login-err", "display-none");
        _innerTextByClass("login-err", messages["captcha.NotBlank"]);
        isClicking = false;
        return;
      }
    }

    /*    if (capKeyVal == "") {
          _getFetch("/open/" + userIdVal).then()
          .then((data) => {
            $loginSubBtn.type = "submit";
            $loginSubBtn.click();
            $loginSubBtn.setAttribute("disabled", "disabled");
            return;
          }).catch((error) => {
            let result = JSON.parse(error.message);
            console.log(result)
            _login.$captchaKey.value = result.captchaKey;
            _removeByClass("captchaCard", "display-none");
            let $imageName = _getElementById("imageName");
            $imageName.src = "/open/image/" + result.imageName;

            _login._captchaBtnClickAddEvent();
            return;
          });
        } */
    if (isClicking) {
      $loginSubBtn.type = "submit";
      $loginSubBtn.click();
      $loginSubBtn.setAttribute("disabled", "disabled");
    }

    isClicking = false;
  }
}

Login.prototype._captchaBtnClickAddEvent = function () {
  let $captchaBtn = _getElementById("captchaBtn");
  let isClicking = false;
  $captchaBtn.onclick = function () {
    let capKeyVal = _login.$captchaKey.value.split(" ").join("");
    if (isClicking) {
      return;
    }
    isClicking = true;

    if (capKeyVal == "") {
      isClicking = false;
      return;
    }

  }
}

