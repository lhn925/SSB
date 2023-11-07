export function AuthInput(errors, auth, t, onKeyUp, authCodeCheckBtn, countDownTime) {
  return <>
    <div className={"input-group form-join form-auth "
        + (errors.authCode.error ? 'error' : auth.success ? ''
            : 'on')}>
      <input type="text" className={"form-control authCode "
          + (errors.authCode.error ? 'border-danger' : '')}
             name="authCode" id="authCode"
             placeholder={t(`msg.common.sky.auth`)}
             disabled={auth.success}
             onKeyUp={onKeyUp}/>
      <div className="input-group-append">
        <button type="button" onClick={authCodeCheckBtn}
                className="btn btn-primary authCode"
                disabled={auth.success}
                id="verifyCodeButton">
          {t(`msg.join.sky.checkBtn`)}
        </button>
      </div>
    </div>
    <small id="verification-msg"
           className={errors.authCode.error ? 'text-danger'
               : ''}>{errors.authCode.message}</small>
    <small id="verification-time"
           className={countDownTime.error ? 'text-danger'
               : ''}>{countDownTime.message}</small>
  </>;
}
