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
  this.$email = _getElementById("email");
}
Email.prototype._sendAuthCodeFetch = function () { // 이메일 인증코드 요청
                                                   // 공백 없앰
  let emailVal = this.$email.value.split(" ").join("");

  let $errorMsg = _getElementById("email-NotThyme-msg");
  let $verificationMsg = _getElementById("verification-msg");
  let $authCode = _getElementById("authCode");

  if (emailVal == "") { // 이메일 형식 확인 및 경고메세지 띄움
    $errorMsg.innerText = errors["NotBlank"];
    _addClassById(_email.$email, "border-danger")
    _addClassByParent(_email.$email, "error");
    return;
  }
  if (!_regex("email", emailVal)) { // 이메일 형식 확인 및 경고메세지 띄움
    $errorMsg.innerText = messages["userJoinForm.email"];
    _addClassById(_email.$email, "border-danger")
    _addClassByParent(_email.$email, "error");
    return;
  }
  $errorMsg.innerText = "";

  _post("/email/join", {email: emailVal})
  .then((data) => {
        if (_email._countdownInterval != null) { // 유효 시간 중단 및 재 시작
          clearInterval(_email._countdownInterval);
        }
        // 오류시 border-danger 제거
        _removeClassById(_email.$email, "border-danger")

        // 형제 error-msg 제거
        _removeNodesByClass("email-thyme-msg");

        // authCode 부모 div error 클래스 제거
        _removeClassByParent($authCode, "error");
        _removeClassByParent(_email.$email, "error");

        // 오류메시지 제거
        $verificationMsg.innerText = "";

        _email._emailDto = data;
        _join.$isChkEmail.checked = true;
        // 사용자가 이메일을 보낸 후, 인증 코드 유효시간 - 인증 코드 발급시간 / 1000
        let verifyTime = (new Date(_email._emailDto.data.authTimeLimit)
            - new Date(_email._emailDto.data.authIssueTime)) / 1000
        let $countdown = _getElementById("verification-time");
        _email._startCountdown(verifyTime, $countdown);
      }
  ).catch((error) => {
    _email._errorResult = JSON.parse(error.message);
    _removeNodesByClass("email-Thyme-msg");
    _addClassByParent(_email.$email, "error");
    let message = _email._errorResult.message;
    // 에러메시지
    $errorMsg.innerText = message;
    _join.$isChkEmail.checked = false;
  })
}

/**
 * clickCheck : true 인증번호 확인 버튼을 눌렀을경우
 * clickCheck : false 인증 하지 않고 가입하기 버튼을 눌렀을 경우
 */

Email.prototype._reqEmailAuthFetch = function (clickCheck) { // 인증번호 체크 함수
  let $authCode = _getElementById("authCode");
  let $verificationMsg = _getElementById("verification-msg");
  let $countdown = _getElementById("verification-time");

  if (!clickCheck) { // submit 버튼을 눌렀는데 인증이 되지 않았을 경우 false가 전해짐
    $verificationMsg.innerText = messages["userJoinForm.email2"];
    _addClassByClass("form-auth", "error");
    $verificationMsg.className = "text-danger";
    return;
  }

  // 공백 없앰
  let authCodeVal = $authCode.value.split(" ").join("");

  if (authCodeVal == "") {
    $verificationMsg.className = "text-danger";
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
        //인증 성공시 success 저장
        _join.$isChkAuth.checked = data.data.isSuccess;

        _addAttributeByClass("disabled", true, "authCode");
        _email.$email.setAttribute("readOnly", "readOnly");
        _removeByClass("form-auth", "on") // 성공시 이모티콘 변경
        _removeByClass("form-auth", "error") // 실패 error 제거
      }
  ).catch((error) => {
    if (error.name == "Error") {
      _join.$isChkAuth.checked = false;
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
      _removeClassById($countdown, "text-danger");
      // 인증 시간 만료시 수행할 작업 추가
    }
  }, 1000);

}

Email.prototype._sendCodeButtonOnclickEvent = function () { //  sendCodeButton 에 이벤트 구현
  // 중복 클릭 방지
  let isClicking = false;

  _getElementById("sendCodeButton").onclick = function () {
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

  _getElementById("verifyCodeButton").onclick = function () {
    if (isClicking) {
      return;
    }
    isClicking = true;

    // 1초마다 딜레이
    setTimeout(function () {
      isClicking = false
    }, 1000);
    _email._reqEmailAuthFetch(true);
  };
}