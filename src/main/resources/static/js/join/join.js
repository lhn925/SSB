function Join() {

  this._init();

}

Join.prototype._init = function () {
  this._checkValidUserId();
}

// 유저아이디 체크
Join.prototype._checkValidUserId = function () {
  let $elementById = document.getElementById("userId");
  let $idNotThymeMsg = document.getElementById("id-NotThyme-msg");

  // * 아이디: 5~20자의 영문 소문자, 숫자와 특수기호(_),(-)만 사용 가능합니다.
  const idRegex = /^[a-z0-9_-]{5,20}$/;
  $elementById.onkeyup = function () {
    let id = this.value; // 아이디값 갖고오기

    // 아이디 유효검사
    if (!idRegex.test(id)) {
      _addClassByClass("form-id", "error");
      this.classList.add("border-danger");
      $idNotThymeMsg.innerText = messages["userJoinForm.userId"];
      return;
    }
    _get("/duplicate/id", {userId: id})
    .then((data) => {

    }).catch((error) => {
      console.log(error);
    })

  }

}