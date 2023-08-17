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

//해당 element 에 클래스 추가
function _addClassById($elementById, className) {
  $elementById.classList.add(className);
}

//해당 element 에 클래스 삭제
function _removeClassById($elementById, className) {
  $elementById.classList.remove(className);
}

//해당 element 에 부모 태그 클래스 추가
function _addClassByParent($elementById, className) {
  $elementById.parentElement.classList.add(className);
}

//해당 element 에 부모 태크 클래스 삭제
function _removeClassByParent($elementById, className) {
  $elementById.parentElement.classList.remove(className);
}

// 아이디로 element 가져오기
function _getElementById(id) {
  return document.getElementById(id);
}


async function  _getFetch(url) {

  const options = {method: "GET"};

  const res = await fetch(url, options);
  const data = await res.json();

  if (res.ok) {
    return data.data;
  } else {
    throw await Error(JSON.stringify(data.data));
  }
}

