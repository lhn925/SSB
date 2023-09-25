function Picture() {
  this._init();
}

Picture.prototype._init = function () {
  this._pictureFileBtnChangeEvent();
  this._pictureBtnClickAddEvent();
}

Picture.prototype._pictureBtnClickAddEvent = function () {
  let $pictureBtn = document.getElementById("pictureBtn");

  $pictureBtn.onclick = function () {
    let $pictureFile = document.getElementById("pictureFile");
    $pictureFile.click();
  }
}

// 프로필 이미지 교체
Picture.prototype._pictureFileBtnChangeEvent = function () {
  let $pictureFile = document.getElementById("pictureFile");
  $pictureFile.onchange = function () {
    let file = this.files[0];

    let typeCheck = fileImageTypeCheck(file.type);

    if (!typeCheck) {
      _warning(errorsMsg["error.picture"])
      return;
    }
    let isConfirm = confirm(messages["change.picture"]);
    if (!isConfirm) {
      this.value = null;
      return;
    }
    let formData = new FormData();
    formData.append("file", file);

    _filePost("/user/myInfo/api/picture", formData, null).then(
        (data) => {
          _success(messages["picture.success"]);
          _addAttributeByClass("src",
              "/user/file/api/picture/" + data.data.userId,
              "userPicture");
        }
    ).catch((error) => {
      if (error.name == "Error") {
        error = JSON.parse(error.message);
        _error(error.message);
        return false;
      }
    })
  }
}