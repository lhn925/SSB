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
  this.$password = _getElementById("password");
  this.$isChkUId = _getElementById("_isChkUId");
  this.$isChkPw = _getElementById("_isChkPw");
  this.$isChkUname = _getElementById("_isChkUname");
  this.$isChkEmail = _getElementById("_isChkEmail");
  this.$isChkAuth = _getElementById("_isChkAuth");

  this._BtnShowClickAddEvent();
  this.$isChkUId.checked = this._DuplicateCheckAddEvent("userId",
      "id-NotThyme-msg",
      "/user/join/api/duplicate/id?userId=", this.$isChkUId);
  this.$isChkUname.checked = this._DuplicateCheckAddEvent("userName",
      "userName-NotThyme-msg",
      "/user/join/api/duplicate/userName?userName=", this.$isChkUname);
  this.$isChkPw.checked = this._PwSecureCheckAddEvent();
  this._SubmitBtnClickAddEvent();

}

// 회원가입 버튼 클릭시 전체가 유효한값이 입력되었는지 확인하는 함수
Join.prototype._SubmitBtnClickAddEvent = function () {

  let $subBtn = _getElementById("subBtn");
  let isClicking = false;
  $subBtn.onclick = function () {
    if (isClicking) {
      return;
    }
    isClicking = true;

    let _isChkUId = _join.$isChkUId.checked;
    let _isChkPw = _join.$isChkPw.checked;
    let _isChkUname = _join.$isChkUname.checked;
    let _isChkEmail = _join.$isChkEmail.checked;
    let _isChkAuth = _join.$isChkAuth.checked;
    if (!_isChkUId) { // 아이디 유효성 검증이 안 됐을 경우
      _join.$isChkEmail.checked = _join._duplicateCheckFn("userId",
          "id-NotThyme-msg",
          "/user/join/api/duplicate/id?userId=", _getElementById("userId"),
          _join.$isChkUId);
    }

    if (!_isChkPw) { // 비밀번호 유효성 검증
      _join.$isChkPw.checked = _join._PwSecureCheckFn();
    }

    if (!_isChkUname) { // 유저네임 유효성 검증
      _join.$isChkUname.checked = _join._duplicateCheckFn("userName",
          "userName-NotThyme-msg",
          "/user/join/api/duplicate/id?userName=", _getElementById("userName"),
          _join.$isChkUname);
    }

    if (!_isChkEmail) { // 이메일 유효성 검증
      _join.$isChkEmail.checked = _join._duplicateCheckFn("email",
          "email-NotThyme-msg",
          "/user/join/api/duplicate/id?email=", _getElementById("email"),
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
      isClicking = false;
      return;
    }
  }

}

// 패스워드 Input 타입 변경
Join.prototype._BtnShowClickAddEvent = function () {

  let $btnShow = _getElementById("btn-show");
  $btnShow.onclick = function () {

    let isOn = this.classList.contains("on");
    if (isOn) {
      _removeClassById(this, "on");
      $password.type = "password";
      return;
    }
    $password.type = "text";
    _addClassById(this, "on");
  }
}

//패스워드 안전도 체크 이벤트추가
Join.prototype._PwSecureCheckAddEvent = function () {
  this.$password.onkeyup = function () {
    _join._PwSecureCheckFn();
  }
}
//패스워드 안전도 체크
Join.prototype._PwSecureCheckFn = function () {
  let $NotThymeMsg = _getElementById("password-NotThyme-msg");
  let $secureLevel = _getElementById("secureLevel");

  // 해당 클래스 how-secure를 제외 하고 모두 삭제
  $secureLevel.classList.forEach(str => {
    if (str != "how-secure") {
      _removeClassById($secureLevel, str);
    }
  })

  let input_value = $password.value; // 비밀번호 값 갖고오기
  input_value = input_value.split(" ").join("");
  if (input_value == "") {
    _addClassByParent($password, "error");
    _addClassById($password, "border-danger")
    $NotThymeMsg.innerText = errorsMsg["NotBlank"];
    $secureLevel.innerText = "";
    _join.$isChkPw.checked = false;
    return;
  }
  // 8글자이하 16글자 초과시에
  if (input_value.length < 8 || input_value.length >= 17) {
    _addClassByParent($password, "error");
    _addClassById($password, "border-danger");
    $NotThymeMsg.innerText = messages["userJoinForm.password"];
    $secureLevel.innerText = "";
    _join.$isChkPw.checked = false;
    return;
  }

  input_value = input_value.split(" ").join("");
  let secLevel = _PwSecureLevel(input_value);

  let secLevelStr;
  let secLevelClass;
  switch (secLevel) {
    case 0:
      secLevelStr = messages["사용불가"];
      $secureLevel.innerText = secLevelStr;
      _addClassById($secureLevel, "dangerous")
      return false;
    case 1:
      secLevelStr = messages["위험"];
      secLevelClass = "dangerous";
      break;
    case 2:
      secLevelStr = messages["보통"];
      secLevelClass = "normal";
      break;
    case 3:
      secLevelStr = messages["안전"];
      secLevelClass = "safe";
      break;
    default:
      return;
  }

  _removeClassById($password, "border-danger");
  $NotThymeMsg.innerText = "";
  $secureLevel.innerText = secLevelStr;
  _addClassById($secureLevel, secLevelClass);
  _removeClassByParent($password, "error");
  _join.$isChkPw.checked = true;
  return;
}

// 중복 체크 이벤트 추가
Join.prototype._DuplicateCheckAddEvent = function (type, msgId, path,
    $isChkElement) {
  let $elementById = _getElementById(type);
  $elementById.onkeyup = function () {
    _join._duplicateCheckFn(type, msgId, path,
        $elementById, $isChkElement);
  }
}
// 중복체크
Join.prototype._duplicateCheckFn = function (type, msgId, path, $elementById,
    $isChkElement) {
  let $NotThymeMsg = _getElementById(msgId);
  let input_value = $elementById.value; // 아이디값 갖고오기
  // 유효검사
  input_value = input_value.split(" ").join("");

  // 공백값 체크
  if (input_value == "") {
    _addClassByParent($elementById, "error");
    _addClassById($elementById, "border-danger");
    $NotThymeMsg.innerText = errorsMsg["NotBlank"];
    $isChkElement.checked = false;
    return false;
  }

  //정규표현식 체크
  if (!_regex(type, input_value)) {
    _addClassByParent($elementById, "error")
    _addClassById($elementById, "border-danger");
    $NotThymeMsg.innerText = messages["userJoinForm." + type];
    $isChkElement.checked = false;
    return false;
  }
  // 중복 검사
  _get(path + input_value)
  .then((data) => {
    _removeClassById($elementById, "border-danger");
    _removeClassByParent($elementById, "error");
    $NotThymeMsg.innerText = "";
    $isChkElement.checked = true;
    return;
  }).catch((error) => {
    // 중복되거나 사용할수 없는 아이디일 경우
    let joinResult = JSON.parse(error.message);
    _addClassById($elementById, "border-danger");
    _addClassByParent($elementById, "error");
    $NotThymeMsg.innerText = joinResult.message;
    $isChkElement.checked = false;
    return;
  })
}