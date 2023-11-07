import {EmailInput} from "components/input/EmailInput";
import {AuthInput} from "components/input/AuthInput";
import {modalActions} from "../../../store/modalType/modalType";

export default function EmailAuthForm({
  t,
  auth,
  errors,
  onKeyUp,
  emailRef,
  clickBtnSendCode,
  clickBtnAuthCodeCheck,
  countDownTime,
  clickBtnEvent,
  helpType,
  enEmail,
  dispatch
}) {

  return (
      <div className="card">
        <div className="card-body">
          <h4 className="card-title logo-text mainLogo">{t(
              `msg.common.sky.logo`)}</h4>
          {
            helpType === "ID" ? <div className="text-center mb-2 form-text">
                  {t(`msg.help.sky.idDesc`)}<br/></div> :
                <>
              <span className="">{t(
                  `msg.help.sky.emailVerification.body1`)}</span><br/>
                  <span className="mainColor">{enEmail}</span><br/>
                  <span className="form-text">{t(
                      `msg.help.sky.emailVerification.body2`)}</span>
                </>
          }
          <form id="form">
            <div className="form-group">
              <div className="form-group">
                {EmailInput(errors, auth, t, onKeyUp, emailRef,
                    clickBtnSendCode)}
              </div>
              <div className="form-group">
                {AuthInput(errors, auth, t, onKeyUp, clickBtnAuthCodeCheck,
                    countDownTime)}
              </div>
              <div className="d-grid gap-2 col-6 mx-auto">
                <button type="button" id="subBtn" onClick={clickBtnEvent}
                        disabled={!auth.success}
                        className="btn btn-primary btn-block btn-dark mt-5">
                  {helpType === "ID" ? t(`msg.help.sky.idHelp`) : t(
                      `msg.common.sky.nextBtn`)}
                </button>
                <button type="button" onClick={() => {
                  dispatch(modalActions.changeType({type: "LOGIN"}));
                }
                } id="cancelBtn" className="btn btn-outline-info mt-1">{t(
                    `msg.common.sky.cancel`)}</button>

              </div>
            </div>
          </form>
        </div>
      </div>
  );
}

