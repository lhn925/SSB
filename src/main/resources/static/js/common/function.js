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


function _removeNodesByClass(className) { // 자식 노드들중 특정값을 가진 클래스 삭제 함수
  let elements = document.getElementsByClassName(className);
  for (let i = elements.length - 1; i >= 0; i--) {
    let element = elements[i];
    element.parentNode.removeChild(element);
  }
}

function _addAttributeByClass(attribute, value, className) { // 자식 노드들중 특정값을 가진 클래스 삭제 함수
  let elements = document.getElementsByClassName(className);
  for (const element of elements) {
    element.setAttribute(attribute, value);
  }
}
