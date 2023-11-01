//패스워드 안전도 체크 함수
import {useState} from "react";

export function PwSecureCheckFn(level) {
  const secureLevel = {
    secLevelStr: '',
    secLevelClass: ''
  }
  switch (level) {
    case 0:
      secureLevel.secLevelStr = "사용불가";
      secureLevel.secLevelClass = "dangerous";
      break;
    case 1:
      secureLevel.secLevelStr = "위험";
      secureLevel.secLevelClass = "dangerous";
      break;
    case 2:
      secureLevel.secLevelStr = "보통";
      secureLevel.secLevelClass = "normal";
      break;
    case 3:
      secureLevel.secLevelStr = "안전";
      secureLevel.secLevelClass = "safe";
      break;
  }
  return secureLevel;
}

export function PwSecureLevel(password) {// 패스워드 안전 강도
  // 소문자로 이루어졌는데 같은문자만 도배일경우 - 사용불가
  // 그리고 8문자이하면 사용불가 한글 사용불가

  // 소문자인데 2개이상 다른걸로 이루어져 있는데 8글자이면 - 위험
  // 소문자인데 2개이상 다른걸로 이루어져 있는데 10글자이면 - 보통
  // 대문자+소문자,소문자+숫자,대문자로만 이어진 경우,소문자+특수기호,대문자+특수기호 9글자 이하면 보통
  // 소문자인데 2개이상 다른걸로 이루어져 있고 숫자 및 특수기호 포함,대문자 하나라도 포함하고 10글자 이상이면 - 안전

  let secLevel = 0;
  let regex1 = /^(.)\1{1,7}$/ // 같은 문자 반복 및 한글 8글자 이하 차단

  let regex2 = /[A-Z]/g;
  // 대문자 찾기

  let regex3 = /[!@#$%^&*()\-_=+\\|[\]{};:'",.<>/?]/;
  //특수표현식 찾기

  let regex4 = /\d+/g;
  // 숫자 찾기

  let regex5 = /(.)\1{7,}/;
  //연속된 문자열 찾기

  // let regex6 = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*()\-_=+\\|[\]{};:'",.<>/?]).{8,16}$/;
  // //  비밀번호: 8~16자의 영문 대/소문자, 숫자, 특수문자를 사용해 주세요.

  let regex7 = /[ㄱ-ㅎ가-힣]/;
  // 한글확인

  let regex8 = /[\uD800-\uDBFF][\uDC00-\uDFFF]/;
  // 이모지 확인



  /**
   * 0점 사용불가
   * 1점 위험
   * 2점 보통
   * 3점 안전
   *
   */

  /**
   * 소문자로 이루어졌는데 같은문자만 도배일경우 - 사용불가
   * 그리고 8문자이하면 사용불가 한글 사용불가
   */



  if (password.length < 8 || password.length >= 17) {
    secLevel = 0;
    return secLevel;
  }

  /**
   * 이모지 확인
   * 한글 확인
   * 연속된 글자 확인
   * 연속된 문자열 확인
   */
  if (regex8.test(password) ||
      regex7.test(password) ||
      regex1.test(password)) { // 0점 반환
    secLevel = 0;
    return secLevel;
  }

  if (regex2.test(password)) { // 대문자 검색
    console.log("2:" + regex2.test(password))
    ++secLevel;
  }
  if (regex4.test(password)) { // 숫자 검색
    ++secLevel;
  }
  if (regex3.test(password)) { // 특수표현 검색
    ++secLevel;
  }
  console.log(secLevel);
  if (secLevel < 1) {
    if (regex5.test(password)) {
      secLevel = 0;
      return secLevel;
    }
  }

  return secLevel;
}

export function GetCountDownTime(minutes, seconds, remainingTime, setTimer,
    errorMsg) {
  let message = ": 인증 유효시간: " + minutes + ":"
      + seconds;
  if (remainingTime < 1) {
    setTimer(0);
    return {message: errorMsg, error: true};
    // 인증 시간 만료시 수행할 작업 추가
  } else if (remainingTime <= 60) { // 유효시간 1분남을시에 text-color 빨강으로 변경
    return {message: message, error: true};
  } else {
    return {message: message, error: false};
  }

}

export function GetInterval(timer, setTimer ,authTimeLimit, setCountDownTime,
    errorMsg) {
  return setInterval(() => {
    const emailInterValObject = EmailInterValEvent(timer, authTimeLimit);
    const remainingTime = parseInt(emailInterValObject.timer);
    const minutes = emailInterValObject.minutes;
    const seconds = emailInterValObject.seconds;
    const getCountDownTime = GetCountDownTime(minutes, seconds, remainingTime,
        setTimer, errorMsg);
    setCountDownTime(
        {message: getCountDownTime.message, error: getCountDownTime.error});
  }, 1000);
}

export function Regex(type, value) { // 정규표현식 모음
  let regex;
  let testResult;

  switch (type) {
    case "userId": // * 아이디: 5~20자의 영문 소문자, 숫자와 특수기호(_),(-)만 사용 가능합니다.
      regex = /^[a-z0-9_-]{5,20}$/;
      break;
    case "userName":
      regex = /^[a-zA-Z0-9가-힣]{2,8}$/;
      break;
    case "email": //이메일 형식이 아닙니다.
      regex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
      break;
    default:
      return true;
  }

  testResult = regex.test(value);
  return testResult;
}

export function StartCountdown( // 유효시간 5분 알림
    authTimeLimit, authIssueTime) {

  return (new Date(authTimeLimit) - new Date(authIssueTime)) / 1000;
}

export function EmailInterValEvent(timer, authTimeLimit) {
  let minutes, seconds;
  timer = (new Date(authTimeLimit) - new Date()) / 1000;
  minutes = parseInt(timer / 60, 10); // 10진수로 출력
  seconds = parseInt(timer % 60, 10);

  minutes = minutes < 10 ? "0" + minutes : minutes;
  seconds = seconds < 10 ? "0" + seconds : seconds;

  return {
    timer: timer,
    minutes: minutes,
    seconds: seconds
  }

}
