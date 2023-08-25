function Reset() {

  this.$newPw = null;
  this.$newPwChk = null;

  this._init();
}

Reset.prototype._init = function () {
  this.$captchaKey = _getElementById("captchaKey");
  // 인증하고 삭제할때 필요한 이미지 name
  this.$imageName = _getElementById("imageName");
  this.$newPw = _getElementById("newPw");
  this.$newPwChk = _getElementById("newPwChk");

  this._PwResetSubBtnClickAddEvent();
  _captchaBtnClickAddEvent(this.$captchaKey,this.$imageName);
}


// 로그인이시 체크 함수
Login.prototype._PwResetSubBtnClickAddEvent = function () {
  const $loginSubBtn = _getElementById("loginSubBtn");

  let isClicking = false;
  $loginSubBtn.onclick = function () {
    if (isClicking) {
      return;
    }

    isClicking = true;
    // 1초마다 딜레이
    let capKeyVal = _removeWhitespace(_login.$captchaKey.value);

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
