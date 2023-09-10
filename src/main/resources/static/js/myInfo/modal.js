function Modal() {
  this._init();
}

Modal.prototype._init = function () {

}

Modal.prototype._open = function ($modal, $body) {
  window.scrollTo(0, 0);
  $modal.style.display = "flex"
  $body.style.overflow = 'hidden';
}

Modal.prototype._close = function ($modal, $body) {
  $modal.style.display = "none"
  $body.style.overflow = 'auto';
}