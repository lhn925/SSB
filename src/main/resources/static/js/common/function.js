function _onpageshow(path) { // 뒤로가기시 page 초기화 문제
  window.onpageshow = function (event) {
    if (event.persisted) {
      // Back Forward Cache로 브라우저가 로딩될 경우 혹은 브라우저 뒤로가기 했을 경우
      location.href = path;
    } else {
      console.log("발생!");
    }
  }
}

function _regex(type, value) { // 정규표현식 모음
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
      testResult = false;
  }

  testResult = regex.test(value);
  return testResult;

}

function _PwSecureLevel(password) {// 패스워드 안전 강도
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

  if (secLevel < 1) {
    if (regex5.test(password)) {
      secLevel = 0;
      return secLevel;
    }
  }

  return secLevel;
}

function _removeNodesByClass(className) { // 자식 노드들중 특정값을 가진 클래스 삭제 함수
  let elements = document.getElementsByClassName(className);
  for (let i = elements.length - 1; i >= 0; i--) {
    let element = elements[i];
    element.parentNode.removeChild(element);
  }
}

// 해당 클래스가 있는 태그에 속성 추가
function _addAttributeByClass(attribute, value, className) {
  let elements = document.getElementsByClassName(className);
  for (const element of elements) {
    element.setAttribute(attribute, value);
  }
}

// 해당 클래스가 있는 태그에 클래스 삭제
function _removeByClass(className, removeClassName) {
  let elements = document.getElementsByClassName(className);
  for (const element of elements) {
    element.classList.remove(removeClassName);
  }
}

// 해당 클래스가 있는 태그에 클래스 삭제
function _innerTextByClass(className, innerText) {
  let elements = document.getElementsByClassName(className);
  for (const element of elements) {
    element.innerText = innerText;
  }
}

// 해당 클래스가 있는 태그에 클래스 추가
function _addClassByClass(className, addClassName) {
  let elements = document.getElementsByClassName(className);
  for (const element of elements) {
    element.classList.add(addClassName);
  }
}

async function _getFetch(url) {

  const options = {method: "GET"};

  const res = await fetch(url, options);
  const data = await res.json();

  if (res.ok) {
    return data.data;
  } else {
    throw await Error(JSON.stringify(data.data));
  }
}

async function _fetch(method, path, body, headers = {}) { //post fetch
  let url = path;
  const options = {
    method: method,
    headers: {
      "Content-Type": "application/json",
      ...headers,
    },
    body: JSON.stringify(body)
  };
  const res = await fetch(url, options);
  const data = await res.json();
  if (res.ok) {
    return data;
  } else {
    forbiddenTryCatch(res);
    throw Error(JSON.stringify(data.errorDetails[0]));
  }
}

function forbiddenTryCatch(res) {
  if (res.status == HttpStatusCode.FORBIDDEN) {
    alert(errorsMsg["error.unAuth"]);
    let href = location.href;
    location.href = "/login?url=" + href;
  }
}

async function _filePost(path, body) { //post fetch
  let url = path;
  const options = {
    method: "POST",
    headers: {},
    body: body
  };
  const res = await fetch(url, options);
  const data = await res.json();
  if (res.ok) {
    return data;
  } else {
    forbiddenTryCatch(res);
    throw Error(JSON.stringify(data.errorDetails[0]));
  }
}

async function _get(path) { //post fetch
  let url = path;
  const options = {method: "GET"};

  const res = await fetch(url, options);
  const data = await res.json();
  if (res.ok) {
    return data;
  } else {
    forbiddenTryCatch(res);
    throw Error(JSON.stringify(data.errorDetails[0]));
  }
}

function _valueCheck(type, msgId, $elementById,
    $isChkElement) {
  let $NotThymeMsg = document.getElementById(msgId);
  let input_value = $elementById.value; // 아이디값 갖고오기
// 유효검사
  input_value = input_value.split(" ").join("");

// 공백값 체크
  if (input_value == "") {
    $elementById.parentElement.classList.add("error");
    $elementById.classList.add("border-danger");
    $NotThymeMsg.innerText = errorsMsg["NotBlank"];
    $isChkElement.checked = false;
    return false;
  }

//정규표현식 체크
  if (!_regex(type, input_value)) {
    $elementById.parentElement.classList.add("error")
    $elementById.classList.add("border-danger");
    $NotThymeMsg.innerText = messages["userJoinForm." + type];
    $isChkElement.checked = false;
    return false;
  }
}

/**
 * 프로필 사진 타입 체크
 */
function fileImageTypeCheck(type) {
  const fileTypes = ["image/jpg", "image/jpeg", "image/pjpeg", "image/png",
    "image/bmp", "image/x-windows-bmp"]
  for (const fileType of fileTypes) {
    if (fileType === type) {
      return true;
    }
  }
  return false;
}

/**
 * 2023:9:13 수 오후 1:34:20 형식으로 반환
 * @param content
 * @returns {string}
 * @private
 */
function _getFullDateTime(content) {
  let language = navigator.language;
  let timeString = new Date(content.createdDateTime).toLocaleTimeString(
      language);
  let dateString = new Date(
      content.createdDateTime).toLocaleDateString().split(".").join(
      ":").split(" ").join("");
  dateString = dateString.slice(0, dateString.length - 1);
  let localeString = new Date(content.createdDateTime).toLocaleString(
      language, {weekday: 'short'});

  let fullDateTime = dateString + " " + localeString + " " + timeString;
  return fullDateTime;
}

function _getMonth(date) {
  let month = (date.getMonth() + 1) < 10 ? "0" + (date.getMonth() + 1)
      : date.getMonth() + 1;
  return month;
}

function _getDateFormat(date) {

  let getMonth = _getMonth(date);
  let getDay = _getDay(date);
  let value = date.getFullYear() + "-" + getMonth + "-" + getDay;
  return value;
}

function _getDay(date) {
  let day = date.getDate() < 10 ? "0" + date.getDate() : date.getDate();
  return day;
}

/**
 * captcha 재요청 공용 함수
 * @param $captchaKey
 * @param $imageName
 * @private
 */
function _captchaBtnClickAddEvent($captchaKey, $imageName) {
  let $captchaBtn = document.getElementById("captchaBtn");
  let isClicking = false;
  $captchaBtn.onclick = function () {

    if (isClicking) {
      return;
    }
    isClicking = true;
    // 1초마다 딜레이
    setTimeout(function () {
      isClicking = false
    }, 1000);

    let capKeyVal = _removeWhitespace($captchaKey.value);
    if (capKeyVal == "") {
      return;
    }

    let $imagePath = document.getElementById("imagePath");
    let imageName = $imagePath.src;
    if (capKeyVal != "") {
      _getFetch(
          "/nkey/open/again?captchaKey=" + capKeyVal + "&imageName="
          + imageName).then()
      .then((data) => {
        $captchaKey.value = data.captchaKey;
        $imagePath.src = "/nkey/open/image/" + data.imageName;
        $imageName.value = data.imageName;

        return;
      }).catch((error) => {
        _innerTextByClass("error-Thyme-msg", "");
        _removeByClass("login-err", "display-none");
        _innerTextByClass("login-err", errorsMsg["server"])
        return;
      });
    }

  }
}

/**
 * 공백없애줌
 * @param value
 * @returns {string}
 * @private
 */
function _removeWhitespace(value) {
  return value.split(" ").join("");
}

// 패스워드 Input 타입 변경
function _typePwToText($btnShow, ...$pwArray) {

  $btnShow.onclick = function () {
    let isOn = this.classList.contains("on");
    if (isOn) {
      this.classList.remove("on");
      for (const pw of $pwArray) {
        pw.type = "password";
      }
      return;
    }
    for (const pw of $pwArray) {
      pw.type = "text";
    }
    this.classList.add("on");
  }
}

//패스워드 안전도 체크 함수
function _PwSecureCheckFn($isChkPw, $password, notThymeId) {
  let $NotThymeMsg = document.getElementById(notThymeId);
  let $secureLevel = document.getElementById("secureLevel");

  // 해당 클래스 how-secure를 제외 하고 모두 삭제
  $secureLevel.classList.forEach(str => {
    if (str != "how-secure") {
      $secureLevel.classList.remove(str);
    }
  })

  let input_value = $password.value; // 비밀번호 값 갖고오기
  input_value = input_value.split(" ").join("");
  if (input_value == "") {
    $password.parentElement.classList.add("error");
    $password.classList.add("border-danger")
    $NotThymeMsg.innerText = errorsMsg["NotBlank"];
    $secureLevel.innerText = "";
    $isChkPw.checked = false;
    return;
  }
  // 8글자이하 16글자 초과시에
  if (input_value.length < 8 || input_value.length >= 17) {
    $password.parentElement.classList.add("error");
    $password.classList.add("border-danger");
    $NotThymeMsg.innerText = messages["userJoinForm.password"];
    $secureLevel.innerText = "";
    $isChkPw.checked = false;
    return;
  }

  input_value = _removeWhitespace(input_value);
  let secLevel = _PwSecureLevel(input_value);

  let secLevelStr;
  let secLevelClass;
  switch (secLevel) {
    case 0:
      secLevelStr = messages["사용불가"];
      $secureLevel.innerText = secLevelStr;
      $secureLevel.classList.add("dangerous")
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

  $password.classList.remove("border-danger");
  $NotThymeMsg.innerText = "";
  $secureLevel.innerText = secLevelStr;
  $secureLevel.classList.add(secLevelClass);
  $password.parentElement.classList.remove("error");
  $isChkPw.checked = true;
  return;
}

/**
 * 값을 확인 한후
 * subBtn에 submit 타입을 대입
 * @param isClicking
 * @param $subBtn
 * @param $elements
 * @returns {boolean}
 * @private
 */
function _subBtnClick(isClicking, $subBtn, $elements) {
  if (isClicking) {
    return false;
  }
  let isValChk = false;
  isClicking = true;
  // 1초마다 딜레이

  for (const element of $elements) {
    let value = _removeWhitespace(element.value);
    let $element = document.getElementById(element.id + "-msg");
    if (value == "") {
      $element.innerText = messages[element.id + ".NotBlank"];
      isValChk = true;
      isClicking = false;
    } else {
      // $element.classList.add("display-none");
      isValChk = false;
      isClicking = true;
    }
  }
  if (isValChk) {
    return false;
  }

  return isClicking;
}

// 비밀번호 변경시 새 비밀번호와 값이 일치한지 확인
function _PwMatchCheck($newPw, $newPwChk, $isChkNewPwChk) {

  let $element = document.getElementById($newPwChk.id + "-msg");
  if ($newPw.value != "" && $newPw.value != $newPwChk.value) {
    $element.classList.remove(
        "display-none");
    $element.innerText =
        messages[$newPwChk.id + ".NotBlank"];
    $isChkNewPwChk.checked = false;
  } else {
    $isChkNewPwChk.checked = true;
    $element.innerText = "";
    $element.classList.add("display-none");
  }
}