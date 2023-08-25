function Login() {
  this.$userId = null;
  this.$password = null;
  this.$url = null;
  this.$captchaKey = null;
  // 2차인증 코드
  this.$captcha = null;
  this._init();
}

Login.prototype._init = function () {
  this.$userId = _getElementById("userId");
  this.$password = _getElementById("password");
  this.$url = _getElementById("url");
  this.$captchaKey = _getElementById("captchaKey");

  // 인증하고 삭제할때 필요한 이미지 name
  this.$imageName = _getElementById("imageName");
  this.$captcha = _getElementById("captcha");

  this._LoginSubBtnClickAddEvent();
  _captchaBtnClickAddEvent(this.$captchaKey,this.$imageName);
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
    // 1초마다 딜레이
    let userIdVal = _login.$userId.value.split(" ").join("");
    let pwVal = _login.$password.value.split(" ").join("");
    let capKeyVal = _login.$captchaKey.value.split(" ").join("");
    _innerTextByClass("error-Thyme-msg", "");
    if (userIdVal == "") {
      _removeByClass("err-NotThyme-msg", "display-none");
      _innerTextByClass("err-NotThyme-msg", messages["userId.NotBlank"])
      isClicking = false;
      return;
    }
    if (pwVal == "") {
      _removeByClass("err-NotThyme-msg", "display-none");
      _innerTextByClass("err-NotThyme-msg", messages["password.NotBlank"]);
      isClicking = false;
      return;
    }

    if (capKeyVal != "") {
      let captchaVal = _login.$captcha.value.split(" ").join("");
      if (captchaVal == "") {
        _removeByClass("err-NotThyme-msg", "display-none");
        _innerTextByClass("err-NotThyme-msg", messages["captcha.NotBlank"]);

        isClicking = false;
        return;
      }
    }

    if (isClicking) {
      $loginSubBtn.type = "submit";
      $loginSubBtn.click();
      $loginSubBtn.setAttribute("disabled", "disabled");
    }
    setTimeout(function () {
      isClicking = false
    }, 1000);

  }
}

