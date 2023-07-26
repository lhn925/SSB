function Email() {
  this._emailDto = null;
  this._errorResult = null; // error
  this._countdownInterval = null; // 유효시간 변수
  this.$email = null;
  this._init();
}

Email.prototype._init = function () { //sendCodeButton 이벤트 등록
  this._sendCodeButtonOnclickEvent();
  this._verifyCodeButtonEvent();
  this.$email = document.getElementById("email");
}
Email.prototype._sendAuthCodeFetch = function () { // 이메일 인증코드 요청
                                                   // 공백 없앰
  let emailVal = this.$email.value.split(" ").join("");

  let $errorMsg = document.getElementById("email-NotThyme-msg");
  let $verificationMsg = document.getElementById("verification-msg");
  let $authCode = document.getElementById("authCode");

  if (emailVal == "") { // 이메일 형식 확인 및 경고메세지 띄움
    $errorMsg.innerText = errors["NotBlank"];
    return;
  }
  if (!_regex("email", emailVal)) { // 이메일 형식 확인 및 경고메세지 띄움
    $errorMsg.innerText = messages["userJoinForm.email"];
    return;
  }
  $errorMsg.innerText = "";

  _post("/email/join", {email: emailVal})
  .then((data) => {
        if (_email._countdownInterval != null) { // 유효 시간 중단 및 재 시작
          clearInterval(_email._countdownInterval);
        }
        // 오류시 border-danger 제거
        _email.$email.classList.remove("border-danger");

        // 형제 error-msg 제거
        _removeNodesByClass("email-thyme-msg");

        // authCode 부모 div error 클래스 제거
        $authCode.parentElement.classList.remove("error");

        // 오류메시지 제거
        $verificationMsg.innerText = "";

        _email._emailDto = data;
        // 사용자가 이메일을 보낸 후, 인증 코드 유효시간 - 인증 코드 발급시간 / 1000
        let verifyTime = (new Date(_email._emailDto.data.authTimeLimit)
            - new Date(_email._emailDto.data.authIssueTime)) / 1000
        let $countdown = document.getElementById("verification-time");
        _email._startCountdown(verifyTime, $countdown);
      }
  ).catch((error) => {
    _email._errorResult = JSON.parse(error.message);
    _removeNodesByClass("email-Thyme-msg");
    let message = _email._errorResult.message;
    // 에러메시지
    $errorMsg.innerText = message;
  })
}

Email.prototype._reqEmailAuthFetch = function () { // 인증번호 체크 함수
  let $authCode = document.getElementById("authCode");
  let $verificationMsg = document.getElementById("verification-msg");
  let $countdown = document.getElementById("verification-time");

  // 공백 없앰
  let authCodeVal = $authCode.value.split(" ").join("");

  if (authCodeVal == "") {
    $verificationMsg.innerText = messages["authCode.NotBlank"];
    return;
  }

  _post("/email/codeCheck", {authCode: authCodeVal}).then(
      (data) => {
        $verificationMsg.innerText = messages["auth.success"];
        $verificationMsg.className = "success-msg";
        clearInterval(_email._countdownInterval);// 유효시간 중단
        $countdown.innerText = "";
        // 인증 성공시 disabled 속성추가
        _addAttributeByClass("disabled", true, "authCode");
        _email.$email.setAttribute("readOnly", "readOnly");
        _removeByClass("form-auth", "on") // 성공시 이모티콘 변경
        _removeByClass("form-auth", "error") // 실패 error 제거
      }
  ).catch((error) => {
    if (error.name == "Error") {
      error = JSON.parse(error.message);
      _addClassByClass("form-auth", "error");
      $verificationMsg.className = "text-danger";
      $verificationMsg.innerText = error.message;
    }
  })
}

Email.prototype._startCountdown = function (verifyTime, // 유효시간 5분 알림
    $countdown) {
  let timer = verifyTime;
  let minutes, seconds;

  this._countdownInterval = setInterval(function () {
    minutes = parseInt(timer / 60, 10); // 10진수로 출력
    seconds = parseInt(timer % 60, 10);

    minutes = minutes < 10 ? "0" + minutes : minutes;
    seconds = seconds < 10 ? "0" + seconds : seconds;

    $countdown.innerText = ": 인증 유효시간: " + minutes + ":" + seconds;

    if (timer == 60) { // 유효시간 1분남을시에 text-color 빨강으로 변경
      $countdown.className += " text-danger";
    }

    if (--timer < 0) {
      clearInterval(_email._countdownInterval);
      $countdown.innerText = "인증 시간이 만료되었습니다.";
      $countdown.classList.remove("text-danger");
      // 인증 시간 만료시 수행할 작업 추가
    }
  }, 1000);

}

Email.prototype._sendCodeButtonOnclickEvent = function () { //  sendCodeButton 에 이벤트 구현
  // 중복 클릭 방지
  let isClicking = false;

  document.getElementById("sendCodeButton").onclick = function () {
    if (isClicking) {
      return;
    }
    isClicking = true;

    // 1초마다 딜레이
    setTimeout(function () {
      isClicking = false
    }, 1000);

    _email._sendAuthCodeFetch();
  };
}

Email.prototype._verifyCodeButtonEvent = function () { //  sendCodeButton 에 이벤트 구현
                                                       // 중복 클릭 방지
  let isClicking = false;

  document.getElementById("verifyCodeButton").onclick = function () {
    if (isClicking) {
      return;
    }
    isClicking = true;

    // 1초마다 딜레이
    setTimeout(function () {
      isClicking = false
    }, 1000);

    _email._reqEmailAuthFetch();
  };
}