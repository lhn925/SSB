import {useRef} from "react";
import CaptchaApi from "utill/api/captcha/CaptchaApi";
import {toast} from "react-toastify";

export default function Captcha(props) {
  const variable = useRef({
    isDoubleClick: false // 더블 클릭 방지
  })

  function captchaBtnClick() {
    if (variable.current.isDoubleClick) {
      return;
    }
    variable.current.isDoubleClick = true;
    CaptchaApi(props.captchaKey, props.imageName)
    .then(data => {
      props.setImageName(data.data.imageName);
      props.setCaptchaKey(data.data.captchaKey);
    }).catch(() => {
      toast.error(props.t(`errorMsg.server`))
    }).finally(() => {
      variable.current.isDoubleClick = false;
    })
  }

  return (
      <>
        {
          props.type === "LOGIN" ? <LoginCaptcha props={props}
                                                 captchaBtnClick={captchaBtnClick}/> :
              <PwCaptcha props={props} captchaBtnClick={captchaBtnClick}/>
        }
      </>
  );
}

function LoginCaptcha(props) {
  return (
      <div className="card captchaCard">
        <div className="form-group card-body">
          <img className="captcha-img mb-3" id="imagePath"
               src={"./Nkey/open/image/" + props.props.imageName}/>
          <div className="input-group form-login form-cap">
            <input type="text" placeholder={props.props.placeholder}
                   value={props.props.captcha} onChange={(e) => {
              props.props.setCaptcha(e.target.value)
            }}
                   className="form-control captcha"/>
            <div className="input-group-append">
              <button type="button" className="btn captchaBtn"
                      id="captchaBtn" onClick={() => {
                props.captchaBtnClick();
              }}>
              </button>
            </div>
          </div>
        </div>
      </div>);

}

function PwCaptcha(props) {

  return (<div className="card captchaCard">
    <div className="form-group card-body">
      <img className="captcha-img mb-3" id="imagePath"
           src={"./Nkey/open/image/" + props.props.imageName}/>
      <div className={"input-group form-login form-cap " + (props.props.error
          ? "error" : "")}>
        <input type="text" placeholder={props.props.placeholder}
               onKeyUp={props.props.onKeyUp} name="captcha" id="captcha"
               className={"form-control captcha " + (props.props.error
                   ? 'border-danger' : '')}/>
        <div className="input-group-append">
          <button type="button" className="btn captchaBtn"
                  id="captchaBtn" onClick={() => {
            props.captchaBtnClick();
          }}>
          </button>
        </div>
      </div>
      <div className="form-text text-danger">
        <small id="id-NotThyme-msg">{props.props.error ? props.props.message
            : ''}</small>
      </div>
    </div>
  </div>);

}
