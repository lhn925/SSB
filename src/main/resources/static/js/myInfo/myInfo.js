function MyInfo() {
  this.$userNameModal = null;
  this.body = null;
  this.$changeUserNameBtn = null;
  this.$userManageModal = null;
  this.userManageBtn = null;

  this.$userManageModal = null;

  //
  this.modalType = null;
  this._paging = null;

  this.startDate = null;
  this.endDate = null;
  this.$startDate = null;
  this.$endDate = null;

  this._init();

  this.modal = new Modal();
}

MyInfo.prototype._init = function () {
  this.$userNameModal = document.getElementById("userNameModal");
  this.body = document.querySelector("body")
  this.$changeUserNameBtn = document.getElementById("changeUserNameBtn");

  this.$userManageModal = document.getElementById("userManageModal");
  this.userManageBtn = document.getElementsByClassName("userManageBtn");

  this.$startDate = document.getElementById("startDate");
  this.$endDate = document.getElementById("endDate");

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
  _get("/users/myInfo/api/loginDevice?offset=" + offset)
  .then((data) => {
    const pageData = data.data;
    let isEmpty = pageData.empty;
    if (!isEmpty) {
      let contents = pageData.content; // 갖고온 컨텐츠

      let $userManagePaging = _myInfo.$userManageModal.querySelector(
          "#userManagePaging");
      _myInfo._loginDeviceInnerHtml(contents);
      _myInfo._pagingSetting(pageData, $userManagePaging)
    } else { // 컨텐츠가 없으면
      _myInfo._loginDeviceEmpty();
    }
  }).catch((error) => {
    error = JSON.parse(error.message);
    _error(error.message);
    _myInfo.modal._close(_myInfo.$userManageModal, _myInfo.body);
  });
}

MyInfo.prototype._pagingSetting = function (pageData, $userManagePaging) {
  if (_myInfo._paging == null) {
    _myInfo._paging = new Paging(pageData,5);
    _myInfo._paging._pagingInnerHtml($userManagePaging);
  } else {
    _myInfo._paging.pageNumber = pageData.pageable.pageNumber;
  }
  // 페이징 이벤트 추가
  _myInfo._pagingNumberClickAddEvent();
  // 이전페이지 다음페이지 버튼 존재 여부 확인후 이벤트 추가
  if (_myInfo._paging.prevPage || _myInfo._paging.nextPage) {
    _myInfo._pagingViewBtnClickAddEvent();
  }
}

/**
 * 로그인 기기 목록 가져오기
 * @param offset
 * @private
 */
MyInfo.prototype._getPatchUserLogList = function (type, startDate, endDate,
    offset) {
  _get("/users/myInfo/api/userLog?offset=" + offset + "&startDate="
      + startDate + "&endDate=" + endDate + "&type=" + type)
  .then((data) => {
    const pageData = data.data;
    let isEmpty = pageData.empty;
    if (!isEmpty) {
      let contents = pageData.content; // 갖고온 컨텐츠
      let $userManagePaging = _myInfo.$userManageModal.querySelector(
          "#userManagePaging");
      _myInfo._userLogInnerHtml(type, contents);
      _myInfo._pagingSetting(pageData, $userManagePaging);
      _myInfo._searchDateBtnClickAddEvent();
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
      let $additionalInfo = document.getElementById("additionalInfo");
      let $rangeDate = document.getElementById("rangeDate");
      if (_myInfo.modalType === "loginDevice") {
        _myInfo._getPatchLoginDeviceList(offset)
      } else {
        let today = new Date();
        let start = new Date(today);
        let end = new Date(today);
        // 최소 제한 날짜
        let min = new Date(today);

        $rangeDate.classList.remove("display-none");
        $additionalInfo.classList.remove("display-none");
        $additionalInfo.innerText = messages[_myInfo.modalType + ".info"];
        // 처음 에는 일주일전까지의 로그 보여줌

        let minMonthRange = 6;
        if (_myInfo.modalType === "userLoginLog") {
          minMonthRange = 3;
        }
        min.setMonth((today.getMonth() + 1) - minMonthRange);
        start.setDate(today.getDate() - 7);

        // formatter 오류방지
        let minMonth = min.getMonth() < 10 ? "0" + min.getMonth()
            : min.getMonth() + 1;

        // formatter 오류방지
        let minDay = _getDay(min);

        let startVal = _getDateFormat(start);
        let endVal = _getDateFormat(end);
        let minVal = min.getFullYear() + "-" + minMonth + "-"
            + minDay;
        _myInfo.$startDate.valueAsDate = start;
        _myInfo.$endDate.valueAsDate = end;

        _myInfo.$endDate.max = endVal;
        _myInfo.$startDate.max = endVal;

        _myInfo.$startDate.min = minVal;
        _myInfo.$endDate.min = minVal;

        _myInfo.startDate = startVal;
        _myInfo.endDate = endVal;
        _myInfo._getPatchUserLogList(_myInfo.modalType, _myInfo.startDate,
            _myInfo.endDate,
            offset);
      }
      document.getElementById(
          "userManageTitle").innerText = messages[_myInfo.modalType];
      _myInfo.modal._open($userManagerModal, body);
    };
  }

}

MyInfo.prototype._searchDateBtnClickAddEvent = function () {
  let isClicking = false;
  document.getElementById("searchDateBtn").onclick = function () {
    if (isClicking) {
      return;
    }
    isClicking = true;
    // 1초마다 딜레이
    setTimeout(function () {
      isClicking = false
    }, 1000);

    if (_myInfo.modalType !== "userLoginLog" && _myInfo.modalType
        !== "userActivityLog") {
      return;
    }

    let startDate = _myInfo.$startDate.valueAsDate;
    let endDate = _myInfo.$endDate.valueAsDate;


    // endDate startDate 작은 경우
    if (startDate.getTime() > endDate.getTime()) {
      _error(errorsMsg["error.dateError"]);
      return;
    }

    let startVal = _getDateFormat(startDate);
    let endVal = _getDateFormat(endDate);
    _myInfo.startDate = startVal;
    _myInfo.endDate = endVal;

    _myInfo._paging = null;
    _myInfo._getPatchUserLogList(_myInfo.modalType, startVal, endVal, 0);
  }
}

/**
 * 내용 없음 출력
 * @private
 */
MyInfo.prototype._loginDeviceEmpty = function () {
  let $content = this.$userManageModal.querySelector(
      "#content");
  let $userManagePaging = this.$userManageModal.querySelector(
      "#userManagePaging");
  //table 초기화
  //페이지 초기화
  $content.innerHTML = "<div class=\"table-responsive\">\n"
      + "     <table class=\"table table-striped\"> <tr><td colspan='5'>내용 없음<td></tr> "
      + "   </table></div>";
  $userManagePaging.innerHTML = "<li class='page-item'><a class='page-link text-dark bg-gray-300' href='#'>1</a></li>";
}
/**
 *
 * 로그인 되어 있는 기기 목록 html에 추가
 * @param contents
 * @private
 */
MyInfo.prototype._loginDeviceInnerHtml = function (contents) {
  let $content = this.$userManageModal.querySelector(
      "#content");
  $content.innerHTML = "";// 초기화

  for (const content of contents) {
    let fullDateTime = _getFullDateTime(content);
    let trTag =
        " <div class=\"table-responsive\">\n"
        + "          <table class=\"table table-striped\">\n"
        + "            <tbody>\n"
        + "            <tr>\n"
        + "              <td><strong>" + messages["sky.os"] + "</strong></td>\n"
        + "              <td><strong>" + messages["sky.browser"]
        + "</strong></td>\n"
        + "            </tr>\n"
        + "            <tr>\n"
        + "              <td>" + content.os + "</td>\n"
        + "              <td>" + content.browser + "</td>\n"
        + "            </tr>\n"
        + "            </tbody>\n"
        + "          </table>\n"
        + "        </div>"
        + " <div class=\"table-responsive\">\n"
        + "          <table class=\"table table-striped\">\n"
        + "            <tbody >\n"
        + "            <tr>\n"
        + "              <td><strong>" + messages["sky.login.ip"]
        + "</strong></td>\n"
        + "              <td><strong>" + messages["sky.first.login"]
        + "</strong></td>\n"
        + "              <td><strong>" + messages["sky.manage"]
        + "</strong></td>\n"
        + "            </tr>\n"
        + "            <tr>\n"
        + "              <td> " + content.ip + "(" + content.countryName
        + ")</td>\n"
        + "              <td>" + fullDateTime + "</td>\n";
    if (!content.inSession) {
      trTag += "<td><button class='btn btn-primary deviceLogoutBtn'  data-id='"
          + content.session + "' >"
          + messages["sky.logout"] + "</button></td>";
    } else {
      trTag += "<td>" + messages["sky.inSession"] + "</td>";
    }
    trTag += "</tr>\n"
        + "            </tbody>\n"
        + "          </table>\n"
        + "        </div>";
    $content.innerHTML += trTag;
  }
  this._deviceLogoutBtnClickAddEvent();
}

MyInfo.prototype._userLogInnerHtml = function (modalType, contents) {
  let $content = this.$userManageModal.querySelector(
      "#content");
  $content.innerHTML = "";// 초기화

  for (const content of contents) {
    let fullDateTime = _getFullDateTime(content);
    let titleCode1 = "sky.login.ip";
    let titleCode2 = "sky.countryName";
    let titleCode3 = "sky.device";

    let content1;
    let content2;

    if (modalType === "userLoginLog") {
      content1 = content.countryCode;
      content2 = content.userAgent;
    } else {
      content1 = content.chaContent;
      content2 = content.chaMethod;
      titleCode1 = "sky.chaIp";
      titleCode2 = "sky.chaContent";
      titleCode3 = "sky.chaMethod";
    }
    let trTag =
        " <div class=\"table-responsive\">\n"
        + "          <table class=\"table table-striped\">\n"
        + "            <tbody>\n"
        + "            <tr>\n"
        + "              <td><strong>" + messages["sky.createdDate"]
        + "</strong></td>\n"
        + "              <td><strong>" + messages[titleCode1]
        + "</strong></td>\n"
        + "            </tr>\n"
        + "            <tr>\n"
        + "              <td>" + fullDateTime + "</td>\n"
        + "              <td>" + content.ip + "</td>\n"
        + "            </tr>\n"
        + "            </tbody>\n"
        + "          </table>\n"
        + "        </div>"
        + " <div class=\"table-responsive\">\n"
        + "          <table class=\"table table-striped\">\n"
        + "            <tbody >\n"
        + "            <tr>\n"
        + "              <td><strong>" + messages[titleCode2]
        + "</strong></td>\n"
        + "              <td><strong>" + messages[titleCode3]
        + "</strong></td>\n"
        + "            </tr>\n"
        + "            <tr>\n"
        + "              <td> " + content1 + "</td>\n"
        + "              <td>" + content2 + "</td>\n"
        + "</tr>\n"
        + "            </tbody>\n"
        + "          </table>\n"
        + "        </div>";
    $content.innerHTML += trTag;
  }
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
      } else {
        _myInfo._getPatchUserLogList(_myInfo.modalType, _myInfo.startDate,
            _myInfo.endDate, offset);
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

  // 현재 페이지 테이블 갯수
  let tableCount = document.getElementsByClassName("table-responsive").length;
  let isClicking = false;
  for (const btn of deviceLogoutBtnList) {
    btn.onclick = function () {
      // 부모의 data id값

      if (isClicking) {
        return;
      }
      isClicking = true;
      // 1초마다 딜레이
      setTimeout(function () {
        isClicking = false
      }, 1000);

      let data = this.dataset.id
      let isLogout = confirm(messages["device.logout"]);
      if (!isLogout) {
        return;
      }
      _fetch("PATCH", "/users/myInfo/api/login/status", {session: data})
      .then(() => {
        _success(messages["device.logout.success"]);

        let pageNumber = _myInfo._paging.pageNumber;

        if (_myInfo._paging.pageNumber != 0) { // 현재페이지가 0인경우 그대로 출력
          pageNumber = tableCount == 2 ? pageNumber - 1 : pageNumber;
        }  // 아닌 경우
        _myInfo._paging = null;
        _myInfo._getPatchLoginDeviceList(pageNumber);
        // 해당 태그 삭제
        this.parentElement.parentElement.parentElement.remove();
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
  let isClicking = false;
  for (const btn of pageItemList) {
    btn.onclick = function () {
      if (isClicking) {
        return;
      }
      isClicking = true;
      // 1초마다 딜레이
      setTimeout(function () {
        isClicking = false
      }, 1000);
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
      } else {
        _myInfo._getPatchUserLogList(_myInfo.modalType, _myInfo.startDate,
            _myInfo.endDate, offset);
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
  let $additionalInfo = document.getElementById("additionalInfo");
  let $rangeDate = document.getElementById("rangeDate");
  for (const closeBtnListElement of closeBtnList) {
    closeBtnListElement.addEventListener("click", e => {
      _myInfo._loginDeviceEmpty($userManageModal);
      _myInfo._paging = null;
      _myInfo.modalType = null;
      $additionalInfo.classList.add("display-none");
      $rangeDate.classList.add("display-none")
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
    location.href = "/users/myInfo/pw";
  }
}
/**
 * 해외 로그인 차단
 * @private
 */
MyInfo.prototype._blockCheckedChangeAddEvent = function () {
  this.$isLoginBlocked.onchange = function () {
    let isChecked = this.checked;
    _fetch("POST", "/users/myInfo/api/block", {isLoginBlocked: isChecked})
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
  let isClicking = false;
  saveBtn.addEventListener("click", e => {
    if (isClicking) {
      return;
    }
    isClicking = true;
    // 1초마다 딜레이
    setTimeout(function () {
      isClicking = false
    }, 1000);
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
    _fetch("POST", "/users/myInfo/api/userName", {userName: value}, null)
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
