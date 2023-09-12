function MyInfo() {
  this.$userNameModal = null;
  this.body = null;
  this.$changeUserNameBtn = null;
  this.$loginDeviceModal = null;
  this.$loginDeviceBtn = null;
  this._paging = null;
  this._init();

  this.modal = new Modal();
}

MyInfo.prototype._init = function () {
  this.$userNameModal = document.getElementById("userNameModal");
  this.$loginDeviceModal = document.getElementById("loginDeviceModal");
  this.body = document.querySelector("body")
  this.$changeUserNameBtn = document.getElementById("changeUserNameBtn");
  this.$loginDeviceBtn = document.getElementById("loginDeviceBtn");
  this.$changePwBtn = document.getElementById("changePwBtn");
  this.$isLoginBlocked = document.getElementById("isLoginBlocked1");
  this._changeUserNameBtnClickAddEvent(this.$userNameModal, this.body);
  this._modalCancelBtnClickAddEvent(this.$userNameModal, this.body);
  this._modalSaveBtnClickAddEvent(this.$userNameModal, this.body);
  const offset = 0;
  this._loginDeviceBtnClickAddEvent(this.$loginDeviceModal,
      this.body, offset);
  this._loginDeviceModalCancelBtnClickAddEvent(this.$loginDeviceModal,
      this.body);
  this._blockCheckedChangeAddEvent();
  this._changePwBtnClickAddEvent();
}

/**
 * 닉네임 변경 모달창 open
 * @param $userNameModal
 * @param body
 * @private
 */
MyInfo.prototype._changeUserNameBtnClickAddEvent = function ($userNameModal,
    body) {
  this.$changeUserNameBtn.addEventListener("click", e => {
    _myInfo.modal._open($userNameModal, body);
  })
}

/**
 * 로그인 기기 목록 가져오기
 * @param offset
 * @private
 */
MyInfo.prototype._getPatchLoginDeviceList = function (offset) {
  _get("/user/myInfo/api/loginDevice?offset=" + offset)
  .then((data) => {
    const pageData = data.data;
    let isEmpty = pageData.empty;
    if (!isEmpty) {
      let contents = pageData.content; // 갖고온 컨텐츠

      let $loginDevicePaging = _myInfo.$loginDeviceModal.querySelector(
          "#loginDevicePaging");
      _myInfo._loginDeviceInnerHtml(contents);
      if (_myInfo._paging == null) {
        _myInfo._paging = new Paging(pageData);
        _myInfo._paging._pagingInnerHtml($loginDevicePaging);
      } else {
        _myInfo._paging.pageNumber = pageData.pageable.pageNumber;
      }
      _myInfo._pagingNumberClickAddEvent();

    } else { // 컨텐츠가 없으면
      _myInfo._loginDeviceEmpty();
    }

  }).catch((error) => {
    error = JSON.parse(error.message);
    _error(error.message);
    _myInfo.modal._close(_myInfo.$loginDeviceModal, _myInfo.body);
  });
}

/**
 * 로그인 기기관리 modal창 open
 * @param $loginDeviceModal
 * @param body
 * @param offset
 * @private
 */
MyInfo.prototype._loginDeviceBtnClickAddEvent = function ($loginDeviceModal,
    body, offset) {
  this.$loginDeviceBtn.addEventListener("click", e => {
    _myInfo._getPatchLoginDeviceList(offset)
    _myInfo.modal._open($loginDeviceModal, body);
  })
}
/**
 * 내용 없음 출력
 * @private
 */
MyInfo.prototype._loginDeviceEmpty = function () {
  let $loginDeviceTbody = this.$loginDeviceModal.querySelector(
      "#loginDeviceTbody");
  let $loginDevicePaging = this.$loginDeviceModal.querySelector(
      "#loginDevicePaging");
  //table 초기화
  //페이지 초기화
  $loginDeviceTbody.innerHTML = "<tr><td colspan='5'>내용 없음<td><tr>";
  $loginDevicePaging.innerHTML = "<li class='page-item'><a class='page-link text-dark bg-gray-300' href='#'>1</a></li>";
}
/**
 *
 * 로그인 되어 있는 기기 목록 html에 추가
 * @param contents
 * @private
 */
MyInfo.prototype._loginDeviceInnerHtml = function (contents) {
  let $loginDeviceTbody = this.$loginDeviceModal.querySelector(
      "#loginDeviceTbody");
  $loginDeviceTbody.innerHTML = "";// 초기화

  for (const content of contents) {

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

    let trTag = "<tr data-id='" + content.session + "'>" +
        " <td>" + content.os + "</td> " +
        "<td>" + content.browser + "</td>" +
        "<td>" + content.ip + " (" + content.countryName + ")</td>" +
        "<td>" + fullDateTime + "</td>";
    if (!content.inSession) {
      trTag += "<td><button class='btn btn-primary deviceLogoutBtn'>로그아웃</button></td>";
    } else {
      trTag += "<td>현재접속중</td>";
    }
    trTag += "</tr>";
    $loginDeviceTbody.innerHTML += trTag;
  }
  this._deviceLogoutBtnClickAddEvent();
}

/**
 * 기기 로그아웃 버튼
 * @private
 */
MyInfo.prototype._deviceLogoutBtnClickAddEvent = function () {
  let deviceLogoutBtnList = document.getElementsByClassName("deviceLogoutBtn");
  for (const btn of deviceLogoutBtnList) {
    btn.onclick = function () {
      // 부모의 data id값
      let trTag = this.parentElement.parentElement;

      let data = trTag.dataset.id
      alert(messages["device.logout"]);
      console.log(data);
      _fetch("PATCH", "/user/myInfo/api/login/status", {session: data})
      .then(() => {
        _success(messages["device.logout.success"]);
        // 해당 태그 삭제
        trTag.remove();
      }).catch((error) => {
        error = JSON.parse(error.message);
        _error(error.message);
      });
    }
  }
}

/**
 * 로그인 기기 페이지 이동 클릭 이벤트
 */
MyInfo.prototype._pagingNumberClickAddEvent = function () {
  let pageItemList = document.getElementsByClassName("page-list");
  for (const btn of pageItemList) {
    btn.onclick = function () {
      // 부모의 data id값
      let offset = this.dataset.id;

      // 페이지 넘버 color class 지우고
      this.parentElement.querySelector(".bg-gray-300").classList.remove(
          "bg-gray-300");

      // 현재 페이지 넘버에 colorClass 추가
      this.querySelector(".page-link").classList.add("bg-gray-300");
      _myInfo._getPatchLoginDeviceList(offset);
    }
  }
}
/**
 * 로그인 기기 관리 모달창 close
 * @param $loginDeviceModal
 * @param body
 * @private
 */
MyInfo.prototype._loginDeviceModalCancelBtnClickAddEvent = function ($loginDeviceModal,
    body) {
  const closeBtnList = $loginDeviceModal.querySelectorAll(".model-cancel")
  for (const closeBtnListElement of closeBtnList) {
    closeBtnListElement.addEventListener("click", e => {
      _myInfo._loginDeviceEmpty($loginDeviceModal);
      _myInfo._paging = null;
      _myInfo.modal._close($loginDeviceModal, body);
    })
  }
}

/**
 * 비밀번호 변경 페이지이동
 * @private
 */
MyInfo.prototype._changePwBtnClickAddEvent = function () {
  this.$changePwBtn.onclick = function () {
    location.href = "/user/myInfo/pw";
  }
}
/**
 * 해외 로그인 차단
 * @private
 */
MyInfo.prototype._blockCheckedChangeAddEvent = function () {
  this.$isLoginBlocked.onchange = function () {
    let isChecked = this.checked;
    _fetch("POST", "/user/myInfo/api/block", {isLoginBlocked: isChecked})
    .then(() => {
      let code = "loginUnblock";
      if (isChecked) {
        code = "loginBlock"
      }
      _success(messages[code]);
    }).catch((error) => {
      error = JSON.parse(error.message);
      _error(error.message);
    })
  }
}

/**
 * 닉네임 변경 모달창 close
 * @param $userNameModal
 * @param body
 * @private
 */
MyInfo.prototype._modalCancelBtnClickAddEvent = function ($userNameModal,
    body) {
  const closeBtnList = $userNameModal.querySelectorAll(".model-cancel")
  for (const closeBtnListElement of closeBtnList) {
    closeBtnListElement.addEventListener("click", e => {
      let $userName = document.getElementById("userName");
      let $ogUserName = document.getElementById("ogUserName");
      let ogValue = _removeWhitespace($ogUserName.value);
      $userName.value = ogValue;
      $userName.text = ogValue;
      _myInfo.modal._close($userNameModal, body);
    })
  }
}
/**
 * 닉네임 변경 버튼 클릭 이벤트
 * @param $userNameModal
 * @param body
 * @private
 */
MyInfo.prototype._modalSaveBtnClickAddEvent = function ($userNameModal, body) {
  const saveBtn = $userNameModal.querySelector(
      ".btn_duo_popup").lastElementChild;
  saveBtn.addEventListener("click", e => {

    let $userName = document.getElementById("userName");
    let $ogUserName = document.getElementById("ogUserName");
    let value = _removeWhitespace($userName.value);
    let ogValue = _removeWhitespace($ogUserName.value);

    // 공백값 체크
    if (value == "") {
      _warning(errorsMsg["NotBlank"]);
      return false;
    }
    if (value === ogValue) {
      $userName.value = ogValue;
      return false;
    }
    //정규표현식 체크
    if (!_regex("userName", value)) {
      _warning(messages["userJoinForm." + "userName"]);
      return false;
    }
    _fetch("POST", "/user/myInfo/api/userName", {userName: value}, null)
    .then((data) => {
      _myInfo.modal._close($userNameModal, body);
      let changeName = data.data.userName;
      $userName.value = changeName;
      $userName.text = changeName;
      $ogUserName.value = changeName;
      let elementsByClassName = document.getElementsByClassName("userNameInfo");

      _success(messages["userName.change.success"], null, 1000);
      _innerTextByClass("userNameInfo", data.data.userName)

    }).catch((error) => {
      error = JSON.parse(error.message);
      _error(error.message);
    });

  })
}
