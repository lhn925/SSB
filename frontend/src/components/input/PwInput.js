export function PwInput({
  handleShowPwChecked,
  name,
  error,
  message,
  isShowPwChecked,
  onKeyUp,
  placeholder,
  passwordRef,
  secLevelClass,
  secLevelStr,type
}) {

  return (
      <>
        <div className={"input-group form-join form-pw "
            + (error ? 'error' : 'on')}>
          <input type={type || "password" } name={name} id={name}
                 placeholder={placeholder}
                 className={"form-control " + (error
                     ? 'border-danger' : '')}
                 onKeyUp={onKeyUp} ref={passwordRef}/>
          <div className="password-info">
            <em className={"how-secure " + secLevelClass}
                name="secureLevel"
                id="secureLevel">{secLevelStr}</em>
            {
              handleShowPwChecked &&
                  <button type="button" id="btn-show"
                          className={"btn-show hide " + (isShowPwChecked ? 'on'
                              : '')} onClick={handleShowPwChecked}>
                    <span className="blind"></span>
                  </button>
            }
          </div>
        </div>
        <div className="form-text text-danger">
          <small id="password-NotThyme-msg"> {error
              ? message : ''}</small>
        </div>
      </>
  )
}