export function EmailInput(errors, auth, t, onKeyUp, emailRef, clickBtnSendCode) {
  return <>
    <div className={"input-group form-join form-email "
        + (errors.email.error ? 'error' : 'on')}>
      <input className={"form-control " + (errors.email.error
          ? 'border-danger' : '')}
             name="email" id="email"
             disabled={auth.success}
             placeholder={t(`msg.common.sky.email`)}
             onKeyUp={onKeyUp} ref={emailRef}/>
      <div className="input-group-append">
        <button type="button" onClick={clickBtnSendCode}
                className="btn btn-primary authCode"
                disabled={auth.success}
                id="sendCodeButton">
          {t(`msg.join.sky.authBtn`)}
        </button>
      </div>
    </div>
    <div className="form-text text-danger">
      <small id="email-NotThyme-msg">{errors.email.error
          ? errors.email.message : ''}</small>
    </div>
  </>;
}
