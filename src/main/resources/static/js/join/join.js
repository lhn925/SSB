function Join() {
  this._init();
  this.$password = null;
}

Join.prototype._init = function () {
  this.$password = document.getElementById("password");

  this._BtnShowAddClickEvent();
  this._DuplicateCheck("userId", "id-NotThyme-msg",
      "/join/api/duplicate/id?userId=");
  this._DuplicateCheck("userName", "userName-NotThyme-msg",
      "/join/api/duplicate/userName?userName=");
  this._PwSecureCheck();

}

// 패스워드 Input 타입 변경
Join.prototype._BtnShowAddClickEvent = function () {

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

//패스워드 안전도 체크
Join.prototype._PwSecureCheck = function () {
//  비밀번호: 8~16자의 영문 대/소문자, 숫자, 특수문자를 사용해 주세요.
  // 소문자로 이루어졌는데 같은문자만 도배일경우 - 사용불가
  // 그리고 8문자이하면 사용불가 한글 사용불가

  // 소문자인데 2개이상 다른걸로 이루어져 있는데 8문장이면 - 위험
  // 소문자인데 2개이상 다른걸로 이루어져 있는데 10문장이면 - 보통
  // 대문자+소문자,소문자+숫자,대문자로만 이어진 경우,소문자+특수기호,대문자+특수기호 9문장 이하면 보통
  // 소문자인데 2개이상 다른걸로 이루어져 있고 숫자 및 특수기호 포함,대문자 하나라도 포함하고 10문장이상이면 - 안전

  let $NotThymeMsg = document.getElementById("password-NotThyme-msg");
  let $secureLevel = document.getElementById("secureLevel");
  this.$password.onkeyup = function () {

    // secure 클래스 삭제
    $secureLevel.classList.forEach(str => {
      if (str != "how-secure") {
        $secureLevel.classList.remove(str);
      }
    })

    let input_value = this.value; // 비밀번호 값 갖고오기
    input_value = input_value.split(" ").join("");
    if (input_value == "") {
      this.parentElement.classList.add("error");
      this.classList.add("border-danger");
      $NotThymeMsg.innerText = errors["NotBlank"];
      $secureLevel.innerText = "";
      return;
    }
    // 8글자이하 16글자 초과시에
    if (input_value.length < 8 || input_value.length >= 17) {
      this.parentElement.classList.add("error");
      this.classList.add("border-danger");
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
    this.classList.remove("border-danger");
    $NotThymeMsg.innerText = "";
    $secureLevel.innerText = secLevelStr;

    $secureLevel.classList.add(secLevelClass);

    this.parentElement.classList.remove("error");

  }

}

// 중복 체크
Join.prototype._DuplicateCheck = function (type, msgId, path) {
  let $elementById = document.getElementById(type);
  let $NotThymeMsg = document.getElementById(msgId);

  $elementById.onkeyup = function () {
    let input_value = this.value; // 아이디값 갖고오기
    // 유효검사

    input_value = input_value.split(" ").join("");

    if (input_value == "") {
      this.parentElement.classList.add("error");
      this.classList.add("border-danger");
      $NotThymeMsg.innerText = errors["NotBlank"];
      return;
    }

    if (!_regex(type, input_value)) {
      this.parentElement.classList.add("error");
      this.classList.add("border-danger");
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
}
