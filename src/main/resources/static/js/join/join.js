function Join() {
  this._init();
  this.$password = null;
}

Join.prototype._init = function () {
  this.$password = document.getElementById("password");

  this._BtnShowClickAddEvent();
  this._DuplicateCheckAddEvent("userId", "id-NotThyme-msg",
      "/join/api/duplicate/id?userId=");
  this._DuplicateCheckAddEvent("userName", "userName-NotThyme-msg",
      "/join/api/duplicate/userName?userName=");
  this._PwSecureCheckAddEvent();

}
Join.prototype._SubmitBtnClickAddEvent = function () {
  


}

// 패스워드 Input 타입 변경
Join.prototype._BtnShowClickAddEvent = function () {

  let isClicking = false;
  let $btnShow = document.getElementById("btn-show");
  $btnShow.onclick = function () {
    if (isClicking) {
      return;
    }
    let isOn = this.classList.contains("on");
    if (isOn) {
      this.classList.remove("on")
      $password.type = "password";
      return;
    }
    $password.type = "text";
    this.classList.add("on");
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
  let $NotThymeMsg = document.getElementById("password-NotThyme-msg");
  let $secureLevel = document.getElementById("secureLevel");
  $secureLevel.classList.forEach(str => {
    if (str != "how-secure") {
      $secureLevel.classList.remove(str);
    }
  })

  let input_value = this.value; // 비밀번호 값 갖고오기
  input_value = input_value.split(" ").join("");
  if (input_value == "") {
    $password.parentElement.classList.add("error");
    $password.classList.add("border-danger");
    $NotThymeMsg.innerText = errors["NotBlank"];
    $secureLevel.innerText = "";
    return;
  }
  // 8글자이하 16글자 초과시에
  if (input_value.length < 8 || input_value.length >= 17) {
    $password.parentElement.classList.add("error");
    $password.classList.add("border-danger");
    $NotThymeMsg.innerText = messages["userJoinForm.password"];
    $secureLevel.innerText = "";
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
      $secureLevel.classList.add("dangerous")
      return;
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
  $password.classList.remove("border-danger");
  $NotThymeMsg.innerText = "";
  $secureLevel.innerText = secLevelStr;

  $secureLevel.classList.add(secLevelClass);

  $password.parentElement.classList.remove("error");
}

// 중복 체크 이벤트 추가
Join.prototype._DuplicateCheckAddEvent = function (type, msgId, path) {
  let $elementById = document.getElementById(type);
  $elementById.onkeyup = function () {
    _join._duplicateCheckFn(type, msgId, path, $elementById);
  }
}
// 중복체크
Join.prototype._duplicateCheckFn = function (type, msgId, path, $elementById) {
  let $NotThymeMsg = document.getElementById(msgId);
  let input_value = $elementById.value; // 아이디값 갖고오기
  // 유효검사

  input_value = input_value.split(" ").join("");

  if (input_value == "") {
    $elementById.parentElement.classList.add("error");
    $elementById.classList.add("border-danger");
    $NotThymeMsg.innerText = errors["NotBlank"];
    return;
  }

  if (!_regex(type, input_value)) {
    $elementById.parentElement.classList.add("error");
    $elementById.classList.add("border-danger");
    $NotThymeMsg.innerText = messages["userJoinForm." + type];
    return;
  }
  // 중복 검사
  _get(path + input_value)
  .then((data) => {
    $elementById.classList.remove("border-danger");
    $elementById.parentElement.classList.remove("error");
    $NotThymeMsg.innerText = "";
  }).catch((error) => {
    // 중복되거나 사용할수 없는 아이디일 경우
    let joinResult = JSON.parse(error.message);
    $elementById.classList.add("border-danger");
    $elementById.parentElement.classList.add("error");
    $NotThymeMsg.innerText = joinResult.message;
  })
}