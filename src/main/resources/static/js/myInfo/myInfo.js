function MyInfo() {
  this._init();
  this.$modal = null;
  this.body = null;
  this.$changeUserNameBtn = null;
  this.modal = new Modal();
}

MyInfo.prototype._init = function () {
  this.$modal = document.getElementById("modal");
  this.body = document.querySelector("body")
  this.$changeUserNameBtn = document.getElementById("changeUserNameBtn");
  this.$changePwBtn = document.getElementById("changePwBtn");
  this._changeUserNameBtnClickAddEvent(this.$modal, this.body);
  this._modalCancelBtnClickAddEvent(this.$modal, this.body);
  this._modalSaveBtnClickAddEvent(this.$modal, this.body);
  this._changePwBtnClickAddEvent();
}

MyInfo.prototype._changeUserNameBtnClickAddEvent = function ($modal, body) {
  this.$changeUserNameBtn.addEventListener("click", e => {
    _myInfo.modal._open($modal, body);
  })
}
MyInfo.prototype._changePwBtnClickAddEvent = function () {
  this.$changePwBtn.onclick = function () {
    location.href = "/user/myInfo/pw";
  }
}
MyInfo.prototype._modalCancelBtnClickAddEvent = function ($modal, body) {
  const closeBtnList = $modal.querySelectorAll(".model-cancel")
  for (const closeBtnListElement of closeBtnList) {
    closeBtnListElement.addEventListener("click", e => {
      let $userName = document.getElementById("userName");
      let $ogUserName = document.getElementById("ogUserName");
      let ogValue = _removeWhitespace($ogUserName.value);
      $userName.value = ogValue;
      $userName.text = ogValue;
      _myInfo.modal._close($modal, body);
    })
  }

}
MyInfo.prototype._modalSaveBtnClickAddEvent = function ($modal, body) {
  const saveBtn = $modal.querySelector(".btn_duo_popup").lastElementChild;
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
    _post("/user/myInfo/api/userName", {userName: value}, null)
    .then((data) => {
      _myInfo.modal._close($modal, body);
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
