function _join_ready_init() {
  _email = new Email();
  _join = new Join();
}

function _agree_ready_init() {
  _agree = new Agree();

}

async function _post(path, body, headers = {}) { //post fetch
  let url = path;
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
    throw Error(JSON.stringify(data.errorDetails[0]));
  }
}
async function _get(path, body, headers = {}) { //post fetch
  let url = path;
  const options = {
    method: "GET",
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
    throw Error(JSON.stringify(data.errorDetails[0]));
  }
}
