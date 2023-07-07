function join_ready_init() {
  _email = new Email();
}

function agree_ready_init() {
  _agree = new Agree();

}

function removeNodesByClass(className) { // 자식 노드들중 특정값을 가진 클래스 삭제 함수
  let elements = document.getElementsByClassName(className);
  for (let i = elements.length - 1; i >= 0; i--) {
    let element = elements[i];
    element.parentNode.removeChild(element);
  }
}

async function post(path, body, headers = {}) { //post fetch
  const url = path;
  const options = {
    method: "POST",
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
    // throw Error(data.errorDetails[0].message);
    throw Error(JSON.stringify(data.errorDetails[0]));
  }
}
