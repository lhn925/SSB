function Idquery() {

  this.$userId = null;

  this._init();
}

Idquery.prototype._init = function () {
  this.$userId = _getElementById("userId");
  this._SubmitBtnClickAddEvent()
}

Idquery.prototype._SubmitBtnClickAddEvent = function () {

  let $subBtn = _getElementById("subBtn");
  let isClicking = false;
  $subBtn.onclick = function () {
    if (isClicking) {
      return;
    }

    isClicking = true;
    // 1초마다 딜레이
    setTimeout(function () {
      isClicking = false
    }, 1000);

    let userIdVal = _idquery.$userId.value;

    userIdVal = userIdVal.split(" ").join("");

    if (userIdVal == "") {
      alert(messages["userId.NotBlank"]);
      return;
    }
    this.type = "submit";
    this.click();
  }

}
