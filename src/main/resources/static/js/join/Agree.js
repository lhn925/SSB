function Agree() {
  this.$sbbAgreement = null;
  this.$infoAgreement = null;
  this._init();
}

Agree.prototype._init = function () {
  this.$sbbAgreement = _getElementById("sbbAgreement1");// 사이트 이용약관 동의
  this.$infoAgreement = _getElementById("infoAgreement1");// 개인정보 이용약관 동의
  this._allCheckBtnEvent();
  this._sbbCheckBtnEvent();
  this._infoCheckBtnEvent();
  this._isChecked();
}

Agree.prototype._allCheckBtnEvent = function () { // 전체 동의 버튼
  let is_checked = false;
  _getElementById("chk_all").onclick = function () {
    let $agreeCheckBox = document.getElementsByClassName("agreeCheck");
    if (!is_checked) {
      is_checked = true;
    } else {
      is_checked = false;
    }
    for (const checkBox of $agreeCheckBox) {
      checkBox.checked = is_checked;
    }
    _agree._isChecked();
  }
}

Agree.prototype._isChecked = function () {
  let sbbChecked = this.$sbbAgreement.checked;
  let infoChecked = this.$infoAgreement.checked;
  let $agreeSubBtn = _getElementById("agreeSubBtn"); // 버튼 비활성화
  if (sbbChecked && infoChecked) {
    $agreeSubBtn.disabled = false;
  } else {
    _getElementById("chk_all").checked = false;
    $agreeSubBtn.disabled = true;
  }
}
Agree.prototype._sbbCheckBtnEvent = function () {
  this.$sbbAgreement.onclick = function () {
    _agree._isChecked();
  }
}
Agree.prototype._infoCheckBtnEvent = function () {
  this.$infoAgreement.onclick = function () {
    _agree._isChecked();
  }
}