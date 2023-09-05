function _setToastr(toastr) {
  toastr.options.escapeHtml = true;
  toastr.options.closeButton = true;
  toastr.options.newestOnTop = false;
  toastr.options.progressBar = true;
}

function _info(content,title) {
  toastr.info(title,content,{timeout:3000});
}
function _success(content,title) {
  toastr.success(title,content,{timeout:3000});
}

function _error(content,title) {
  toastr.error(title,content,{timeout:3000});
}


function _warning(content,title) {
  toastr.warning(title,content,{timeout:3000});
}

