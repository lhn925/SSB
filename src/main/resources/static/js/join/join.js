function Join() {
  this.$password = null;
  this.$isChkUId = null;
  this.$isChkPw = null;
  this.$isChkUname = null;
  this.$isChkEmail = null;
  this.$isChkAuth = null;
  this._init();
}

Join.prototype._init = function () {
  this.$password = document.getElementById("password");
  this.$isChkUId = document.getElementById("_isChkUId");
  this.$isChkPw = document.getElementById("_isChkPw");
  this.$isChkUname = document.getElementById("_isChkUname");
  this.$isChkEmail = document.getElementById("_isChkEmail");
  this.$isChkAuth = document.getElementById("_isChkAuth");

  _typePwToText(document.getElementById("btn-show"), $password);
  this.$isChkUId.checked = this._DuplicateCheckAddEvent("userId",
      "id-NotThyme-msg",
      "/users/join/api/duplicate/id?userId=", this.$isChkUId);
  this.$isChkUname.checked = this._DuplicateCheckAddEvent("userName",
      "userName-NotThyme-msg",
      "/users/join/api/duplicate/userName?userName=", this.$isChkUname);
  this.$isChkPw.checked = this._PwSecureCheckAddEvent();
  this._SubmitBtnClickAddEvent();

}


// 회원가입 버튼 클릭시 전체가 유효한값이 입력되었는지 확인하는 함수
Join.prototype._SubmitBtnClickAddEvent = function () {

  let $subBtn = document.getElementById("subBtn");
  let isClicking = false;
  $subBtn.onclick = function () {
    if (isClicking) {
      return;
    }
    isClicking = true;
    // 1초마다 딜레이
    setTimeout(function () {
      isClicking = false
    }, 1000);

    let _isChkUId = _join.$isChkUId.checked;
    let _isChkPw = _join.$isChkPw.checked;
    let _isChkUname = _join.$isChkUname.checked;
    let _isChkEmail = _join.$isChkEmail.checked;
    let _isChkAuth = _join.$isChkAuth.checked;
    if (!_isChkUId) { // 아이디 유효성 검증이 안 됐을 경우
      _join.$isChkEmail.checked = _join._duplicateCheckFn("userId",
          "id-NotThyme-msg",
          "/users/join/api/duplicate/id?userId=", document.getElementById("userId"),
          _join.$isChkUId);
    }

    if (!_isChkPw) { // 비밀번호 유효성 검증
      _join.$isChkPw.checked = _PwSecureCheckFn(_join.$isChkPw, _join.$password,
          "password-NotThyme-msg");
    }

    if (!_isChkUname) { // 유저네임 유효성 검증
      _join.$isChkUname.checked = _join._duplicateCheckFn("userName",
          "userName-NotThyme-msg",
          "/users/join/api/duplicate/userName?userName=",
          document.getElementById("userName"),
          _join.$isChkUname);
    }

    if (!_isChkEmail) { // 이메일 유효성 검증
      _join.$isChkEmail.checked = _join._duplicateCheckFn("email",
          "email-NotThyme-msg",
          "/users/join/api/duplicate/email?email=", document.getElementById("email"),
          _join.$isChkEmail);
    }

    if (!_isChkAuth) { // authCode 유효성 검증
      _email._reqEmailAuthFetch(false);
    }

    if (_isChkUId && _isChkUname && _isChkPw && _isChkEmail && _isChkAuth) {
      this.type = "submit";
      this.click();
      this.setAttribute("disabled", "disabled");
    } else {
      return;
    }
  }

}

//패스워드 안전도 체크 이벤트추가
Join.prototype._PwSecureCheckAddEvent = function () {
  this.$password.onkeyup = function () {
    _PwSecureCheckFn(_join.$isChkPw, _join.$password, "password-NotThyme-msg");
  }
}

// 중복 체크 이벤트 추가
Join.prototype._DuplicateCheckAddEvent = function (type, msgId, path,
    $isChkElement) {
  let $elementById = document.getElementById(type);
  $elementById.onkeyup = function () {
    _join._duplicateCheckFn(type, msgId, path,
        $elementById, $isChkElement);
  }
}
// 중복체크
Join.prototype._duplicateCheckFn = function (type, msgId, path, $elementById,
    $isChkElement) {
  let $NotThymeMsg = document.getElementById(msgId);
  let input_value = $elementById.value; // 아이디값 갖고오기
  // 유효검사
  input_value = input_value.split(" ").join("");

  // 공백값 체크
  if (input_value == "") {
    $elementById.parentElement.classList.add( "error");
    $elementById.classList.add( "border-danger");
    $NotThymeMsg.innerText = errorsMsg["NotBlank"];
    $isChkElement.checked = false;
    return false;
  }

  //정규표현식 체크
  if (!_regex(type, input_value)) {
    $elementById.parentElement.classList.add( "error")
    $elementById.classList.add( "border-danger");
    $NotThymeMsg.innerText = messages["userJoinForm." + type];
    $isChkElement.checked = false;
    return false;
  }
  // 중복 검사
  _get(path + input_value)
  .then((data) => {
    $elementById.classList.remove( "border-danger");
    $elementById.parentElement.classList.remove("error");
    $NotThymeMsg.innerText = "";
    $isChkElement.checked = true;
    return;
  }).catch((error) => {
    // 중복되거나 사용할수 없는 아이디일 경우
    let joinResult = JSON.parse(error.message);
    $elementById.classList.add( "border-danger");
    $elementById.parentElement.classList.add( "error");
    $NotThymeMsg.innerText = joinResult.message;
    $isChkElement.checked = false;
    return;
  })
}