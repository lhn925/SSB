function PwUpdate() {

  this.$password = null;
  this.$captchaKey = null;
  this.$captcha = null;
  this.$newPw = null;
  this.$newPwChk = null;
  this.$imageName = null;
  this.$resetSubBtn = null;
  this.$modalLogoutBtn = null;
  this.modal = new Modal();
  this._init();
}

PwUpdate.prototype._init = function () {

  this.$modal = document.getElementById("modal");
  this.body = document.querySelector("body")
  this.$password = document.getElementById("password");
  this.$isChkPw = document.getElementById("_isChkPw");

  this.$captchaKey = document.getElementById("captchaKey");
  this.$captcha = document.getElementById("captcha");
  // 인증하고 삭제할때 필요한 이미지 name
  this.$imageName = document.getElementById("imageName");
  this.$newPw = document.getElementById("newPw");
  this.$newPwChk = document.getElementById("newPwChk");
  this.$resetSubBtn = document.getElementById("resetSubBtn");

  this.$isChkNewPw = document.getElementById("_isChkNewPw");
  this.$isChkNewPwChk = document.getElementById("_isChkNewPwChk");

  this.$modalLogoutBtn = document.getElementById("modal-logout-btn");
  _typePwToText(document.getElementById("btn-show"), this.$newPw,
      this.$newPwChk);

  this._PwResetSubBtnClickAddEvent(this.$resetSubBtn, this.$newPw,
      this.$newPwChk, this.$captcha);
  this._cancelBtn();
  this._PwSecureCheckAddEvent();
  this._PwMatchCheckAddEvent();
  this._PwValueCheckAddEvent();
  this._radioCheckBtnClickAddEvent();
  this._subBtnClickAddEvent(this.$resetSubBtn, this.$captcha, this.$newPw,
      this.$newPwChk, this.$password)
  _captchaBtnClickAddEvent(this.$captchaKey, this.$imageName);
}

PwUpdate.prototype._pwValueCheck = function (isClicking) {
  let $pwMsg = document.getElementById("password-msg");
  let pwVal = _pwUpdate.$password.value; // 비밀번호 값 갖고오기
  pwVal = pwVal.split(" ").join("");

  if (pwVal == "") {
    _pwUpdate.$password.parentElement.classList.add("error");
    _pwUpdate.$password.classList.add("border-danger")
    $pwMsg.innerText = errorsMsg["NotBlank"];
    isClicking = false;
  } else {
    _pwUpdate.$password.classList.remove("border-danger");
    $pwMsg.innerText = "";
    _pwUpdate.$password.parentElement.classList.remove("error");
  }
  return isClicking;
}

function _crypto(newPwVal) {
  let key = CryptoJS.enc.Utf8.parse(newPwVal);
  let base64 = CryptoJS.enc.Base64.stringify(key);
  return base64;
}

PwUpdate.prototype._subBtnClickAddEvent = function ($subBtn, ...$elements) {
  let isClicking = false;
  $subBtn.onclick = function () {
    let isChkNewPwVal = _pwUpdate.$isChkNewPw.checked;
    let isChkNewPwChkVal = _pwUpdate.$isChkNewPwChk.checked;

    isClicking = _subBtnClick(isClicking, $subBtn, $elements);
    isClicking = _pwUpdate._pwValueCheck(isClicking);

    // 비밀번호 값 확인
    if (!isChkNewPwVal) {
      _PwSecureCheckFn(_pwUpdate.$isChkNewPw, _pwUpdate.$newPw, "newPw-msg");
      isClicking = false;
    }

    // 새 비밀번호 확인 값 확인
    if (!isChkNewPwChkVal) {
      _PwMatchCheck(_pwUpdate.$newPw, _pwUpdate.$newPwChk,
          _pwUpdate.$isChkNewPwChk);
      isClicking = false;
    }

    if (isClicking) {
      let newPwVal = _crypto(_pwUpdate.$newPw.value);
      let newPwChkVal = _crypto(_pwUpdate.$newPwChk.value);
      let pwVal = _crypto(_pwUpdate.$password.value);
      let captchaVal = _pwUpdate.$captcha.value;
      let captchaValKey = _pwUpdate.$captchaKey.value;
      let imageName = _pwUpdate.$imageName.value;

      let body = {
        password: pwVal,
        newPw: newPwVal,
        newPwChk: newPwChkVal,
        captcha: captchaVal,
        captchaKey: captchaValKey,
        imageName: imageName
      };

      // 비밀번호 변경
      _fetch("POST","/user/myInfo/api/pw", body).then((data) => {
        _pwUpdate.modal._open(_pwUpdate.$modal, _pwUpdate.body);
      }).catch((error) => {
        error = JSON.parse(error.message);
        alert(error.message);
        location.reload();
      });
    }
    setTimeout(function () {
      isClicking = false
    }, 1000);

  }
}
/**
 * 모달창 로그인 이벤트
 * @param $radioCheckBtn
 * @private
 */
PwUpdate.prototype._radioCheckBtnClickAddEvent = function () {
  this.$modalLogoutBtn.onclick = function () {
    let $logoutNoneRadio = document.getElementById("logoutNoneRadio");
    let $allLogoutRadio = document.getElementById("allLogoutRadio");

    let allLogoutChecked = $allLogoutRadio.checked;
    let logoutNonChecked = $logoutNoneRadio.checked;
    if (allLogoutChecked) {
      _fetch("POST","/user/myInfo/api/login/status").then(() =>{
        location.href = "/logout";
      }).catch((error) => {
        error = JSON.parse(error.message);
        _error(error.message);
      })
    } else {
      location.href = "/";
    }
  }
}
// 로그인이시 체크 함수
PwUpdate.prototype._PwResetSubBtnClickAddEvent = function ($subBtn,
    ...$elements) {
  this._subBtnClickAddEvent($subBtn, ...$elements);
}

//패스워드 안전도 체크 이벤트추가
PwUpdate.prototype._PwSecureCheckAddEvent = function () {
  this.$newPw.onkeyup = function () {
    _PwSecureCheckFn(_pwUpdate.$isChkNewPw, _pwUpdate.$newPw, "newPw-msg");
  }
}
//패스워드 안전도 체크 이벤트추가
PwUpdate.prototype._cancelBtn = function () {
  document.getElementById("cancel").onclick = function () {
    location.href = "/user/myInfo";
  }
}

//새 비밀번호와 값이 일치한지 이벤트 추가
PwUpdate.prototype._PwMatchCheckAddEvent = function () {
  this.$newPwChk.onkeyup = function () {
    _PwMatchCheck(_pwUpdate.$newPw, _pwUpdate.$newPwChk,
        _pwUpdate.$isChkNewPwChk);
  }
}
PwUpdate.prototype._PwValueCheckAddEvent = function () {
  this.$password.onkeyup = function () {
    _pwUpdate._pwValueCheck(true)
  }
}

