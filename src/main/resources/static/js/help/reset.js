function Reset() {

  this.$captchaKey = null;
  this.$captcha = null;
  this.$newPw = null;
  this.$newPwChk = null;
  this.$imageName = null;
  this.$resetSubBtn = null;

  this._init();
}

Reset.prototype._init = function () {
  this.$captchaKey = _getElementById("captchaKey");
  this.$captcha = _getElementById("captcha");
  // 인증하고 삭제할때 필요한 이미지 name
  this.$imageName = _getElementById("imageName");
  this.$newPw = _getElementById("newPw");
  this.$newPwChk = _getElementById("newPwChk");
  this.$resetSubBtn = _getElementById("resetSubBtn");

  this.$isChkNewPw = _getElementById("_isChkNewPw");
  this.$isChkNewPwChk = _getElementById("_isChkNewPwChk");

  _BtnShowClickAddEvent(_getElementById("btn-show"), this.$newPw,
      this.$newPwChk);
  this._PwResetSubBtnClickAddEvent(this.$resetSubBtn, this.$newPw,
      this.$newPwChk, this.$captcha);

  this._PwSecureCheckAddEvent();
  this._PwMatchCheckAddEvent();
  this._subBtnClickAddEvent(this.$resetSubBtn, this.$captcha, this.$newPw,
      this.$newPwChk)
  _captchaBtnClickAddEvent(this.$captchaKey, this.$imageName);
}



Reset.prototype._subBtnClickAddEvent = function ($subBtn, ...$elements) {
  let isClicking = false;
  $subBtn.onclick = function () {
    let isChkNewPwVal = _reset.$isChkNewPw.checked;
    let isChkNewPwChkVal = _reset.$isChkNewPwChk.checked;

    isClicking = _subBtnClick(isClicking, $subBtn, $elements);

    // 새 비밀번호 값 확인
    if (!isChkNewPwVal) {
      _PwSecureCheckFn(_reset.$isChkNewPw, _reset.$newPw, "newPw-NotThyme-msg");
      isClicking = false;
    }

    // 새 비밀번호 확인 값 확인
    if (!isChkNewPwChkVal) {
      _PwMatchCheck(_reset.$newPw, _reset.$newPwChk, _reset.$isChkNewPwChk);
      isClicking = false;
    }

    if (isClicking) {
      $subBtn.type = "submit";
      $subBtn.click();
      $subBtn.setAttribute("disabled", "disabled");
    }

    setTimeout(function () {
      isClicking = false
    }, 1000);

  }
}

// 로그인이시 체크 함수
Reset.prototype._PwResetSubBtnClickAddEvent = function ($subBtn, ...$elements) {
  this._subBtnClickAddEvent($subBtn, ...$elements);
}

//패스워드 안전도 체크 이벤트추가
Reset.prototype._PwSecureCheckAddEvent = function () {
  this.$newPw.onkeyup = function () {
    _removeClassByParent(_getElementById("newPw-error-div"), "newPw-Thyme-msg");
    _PwSecureCheckFn(_reset.$isChkNewPw, _reset.$newPw, "newPw-NotThyme-msg");
  }
}

//새 비밀번호와 값이 일치한지 이벤트 추가
Reset.prototype._PwMatchCheckAddEvent = function () {
  this.$newPwChk.onkeyup = function () {
    _PwMatchCheck(_reset.$newPw, _reset.$newPwChk,
        _reset.$isChkNewPwChk);
  }
}