function MyInfo() {
  this.$userNameModal = null;
  this.body = null;
  this.$changeUserNameBtn = null;
  this.$userManageModal = null;
  this.userManageBtn = null;

  this.$userManageModal = null;

  this.modalType = null;
  this._paging = null;
  this._init();

  this.modal = new Modal();
}

MyInfo.prototype._init = function () {
  this.$userNameModal = document.getElementById("userNameModal");
  this.body = document.querySelector("body")
  this.$changeUserNameBtn = document.getElementById("changeUserNameBtn");

  this.$userManageModal = document.getElementById("userManageModal");
  this.userManageBtn = document.getElementsByClassName("userManageBtn");

  this.$changePwBtn = document.getElementById("changePwBtn");
  this.$isLoginBlocked = document.getElementById("isLoginBlocked1");
  this._changeUserNameBtnClickAddEvent(this.$userNameModal, this.body);
  this._modalCancelBtnClickAddEvent(this.$userNameModal, this.body);
  this._modalSaveBtnClickAddEvent(this.$userNameModal, this.body);
  const offset = 0;
  this._userManageBtnClickAddEvent(this.$userManageModal,
      this.body, offset);
  this._loginDeviceModalCancelBtnClickAddEvent(this.$userManageModal,
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

      let $loginDevicePaging = _myInfo.$userManageModal.querySelector(
          "#loginDevicePaging");
      _myInfo._loginDeviceInnerHtml(contents);
      if (_myInfo._paging == null) {
        _myInfo._paging = new Paging(pageData);
        _myInfo._paging._pagingInnerHtml($loginDevicePaging);
      } else {
        _myInfo._paging.pageNumber = pageData.pageable.pageNumber;
      }
      _myInfo._pagingNumberClickAddEvent();
      // 이전페이지 다음페이지 버튼 존재 여부 확인후 이벤트 추가
      if (_myInfo._paging.prevPage || _myInfo._paging.nextPage) {
        _myInfo._pagingViewBtnClickAddEvent();
      }
    } else { // 컨텐츠가 없으면
      _myInfo._loginDeviceEmpty();
    }
  }).catch((error) => {
    error = JSON.parse(error.message);
    _error(error.message);
    _myInfo.modal._close(_myInfo.$userManageModal, _myInfo.body);
  });
}

/**
 * 로그인 기기관리 modal창 open
 * @param $userManagerModal
 * @param body
 * @param offset
 * @private
 */
MyInfo.prototype._userManageBtnClickAddEvent = function ($userManagerModal,
    body, offset) {
  for (const userManage of this.userManageBtn) {
    userManage.onclick = function () {
      _myInfo.modalType = this.dataset.id;
      if (_myInfo.modalType === "loginDevice") {
        _myInfo._getPatchLoginDeviceList(offset)
      } else if (_myInfo.modalType === "userLoginLog") {

      } else {

      }
      _myInfo.modal._open($userManagerModal, body);
    };
  }

}

/**
 * 내용 없음 출력
 * @private
 */
MyInfo.prototype._loginDeviceEmpty = function () {
  let $loginDeviceTbody = this.$userManageModal.querySelector(
      "#loginDeviceTbody");
  let $loginDevicePaging = this.$userManageModal.querySelector(
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
  let $loginDeviceTbody = this.$userManageModal.querySelector(
      "#loginDeviceTbody");
  $loginDeviceTbody.innerHTML = "";// 초기화

  for (const content of contents) {
    let fullDateTime = _getFullDateTime(content);
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

MyInfo.prototype._pagingViewBtnClickAddEvent = function () {
  let pagingViews = document.getElementsByClassName("pagingView");
  for (const pagingView of pagingViews) {
    pagingView.addEventListener("click", function () {
      let offset = this.parentElement.dataset.id;
      if (offset == null || _myInfo.modalType == null) {
        return;
      }
      // paging 처리를 위해 초기화
      _myInfo._paging = null;
      if (_myInfo.modalType === "loginDevice") {
        _myInfo._getPatchLoginDeviceList(offset);
      } else if (_myInfo.modalType === "userLoginLog") {

      } else {

      }
    })
  }
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
        _myInfo._getPatchLoginDeviceList(_myInfo._paging.pageNumber);
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
  let pageItemList = document.getElementsByClassName("notSelected");
  for (const btn of pageItemList) {
    btn.onclick = function () {
      // 부모의 data id값
      let offset = this.dataset.id;

      if (offset == null || _myInfo.modalType == null) {
        return;
      }

      // 현재 페이지넘버에 selected가 있으면 return
      if (this.classList.contains("selected")) {
        return;
      }
      // 페이지 넘버 color class 지우고
      this.parentElement.querySelector(".bg-gray-300").classList.remove(
          "bg-gray-300");

      // 현재페이지는 클릭이 안되게 selected Class추가
      let selected = this.parentElement.querySelector(".selected");
      selected.classList.remove("selected");
      selected.classList.add("notSelected");

      // 현재 페이지 넘버에 colorClass 추가
      this.querySelector(".page-link").classList.add("bg-gray-300");
      this.classList.remove("notSelected");
      this.classList.add("selected")

      if (_myInfo.modalType === "loginDevice") {
        _myInfo._getPatchLoginDeviceList(offset);
      } else if (_myInfo.modalType === "loginLog") {

      } else {

      }

    }
  }
}
/**
 * 로그인 기기 관리 모달창 close
 * @param $userManageModal
 * @param body
 * @private
 */
MyInfo.prototype._loginDeviceModalCancelBtnClickAddEvent = function ($userManageModal,
    body) {
  const closeBtnList = $userManageModal.querySelectorAll(".model-cancel")
  for (const closeBtnListElement of closeBtnList) {
    closeBtnListElement.addEventListener("click", e => {
      _myInfo._loginDeviceEmpty($userManageModal);
      _myInfo._paging = null;
      _myInfo.modalType = null;
      _myInfo.modal._close($userManageModal, body);
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

      _success(messages["userName.change.success"], null, 1000);
      _innerTextByClass("userNameInfo", data.data.userName)

    }).catch((error) => {
      error = JSON.parse(error.message);
      _error(error.message);
    });

  })
}
