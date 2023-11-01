import "modal/css/Login/login.css"
import {useRef, useState} from "react";
import {useTranslation} from "react-i18next";
import {useDispatch} from "react-redux";
import {toast} from "react-toastify";
import CaptchaApi from "utill/api/captcha/CaptchaApi";
import {authActions} from "store/auth/authReducers";
import {useNavigate} from "react-router";
import LoginApi from "utill/api/LoginApi/LoginApi";
import {Link} from "react-router-dom";
import {modalActions} from "store/modalType/modalType";

function Login(props) {

  const {t} = useTranslation();
  const [userId, setUserId] = useState("");
  const [password, setPassword] = useState("");
  const [captcha, setCaptcha] = useState("");
  const [captchaKey, setCaptchaKey] = useState(null);
  const [imageName, setImageName] = useState(null);
  const [errorMsg, setErrorMsg] = useState(null);
  const dispatch = useDispatch();

  const variable = useRef({
    isDoubleClick: false // 더블 클릭 방지
  })

  const navigate = useNavigate();
  const Submit = async () => {
    let rmUserId = userId.split(" ").join("");
    let rmPassword = password.split(" ").join("");
    let rmCaptcha = captcha.split(" ").join("");
    setUserId(rmUserId);
    setPassword(rmPassword);
    setCaptcha(rmCaptcha);

    if (userId == "") {
      setErrorMsg(t(`msg.userId.NotBlank`));
      return;
    }
    if (password == "") {
      setErrorMsg(t(`msg.password.NotBlank`));
      return;
    }
    if (captchaKey != null && captcha == "") {
      setErrorMsg(t(`msg.captcha.NotBlank`));
      return;
    }
    let body = {
      userId: userId,
      password: password,
      captcha: captcha,
      captchaKey: captchaKey,
      imageName: imageName
    };
    if (variable.current.isDoubleClick) {
      return;
    }
    variable.current.isDoubleClick = true;

    let loading = toast.loading("로그인 중...");

    LoginApi(body).then(response => {
      dispatch(authActions.setRefresh(response.data));
      dispatch(authActions.setAccess(response.data));
      dispatch(authActions.setRefreshHeader());
      dispatch(authActions.setAccessHeader());
      toast.dismiss(loading);
      toast.success("로그인 성공 했습니다.");
      props.closeModal();
      navigate("/");
    }).catch(error => {
      let message;
      if (error.response.status == 401) {
        message = error.response.data.message;
        setCaptchaKey(error.response.data.captchaKey);
        setImageName(error.response.data.imageName);
      } else if (error.response.status != 500) {
        let errorDetail = error.response.data.errorDetails[0];
        message = errorDetail.message;
      } else {
        message = t(`errorMsg.server`);
      }
      setCaptcha('');
      toast.dismiss(loading);
      toast.error(message)
      setErrorMsg(message);
    }).finally(() => {
      variable.current.isDoubleClick = false;
    });
  }
  return (
      <div className="card mainCard">
        <div className="card-body">
          <h5 className="card-title text-center">{t(
              `msg.loginForm.sky.login`)}</h5>
          <form className="mt-4" method="post">
            <div className="form-group">
              <div className="input-group form-login form-id">
                <input onKeyUp={(e) => {
                  let value = e.target.value;
                  setUserId(value);
                }} type="text" id="userId" name="userId"
                       className="form-control" placeholder={t(`msg.common.sky.id`)}/>
              </div>
            </div>

            <div className="form-group">
              <div className="input-group form-login form-pw">
                <input type="password"
                       onKeyUp={(e) => {
                  let value = e.target.value;
                  setPassword(value);
                }} className="form-control form-control "placeholder={t(`msg.common.sky.pw`)}/>
              </div>
            </div>
            {
                captchaKey != null && <Captcha imageName={imageName}
                                               setImageName={setImageName} t={t}
                                               captcha={captcha}
                                               setCaptcha={setCaptcha}
                                               setCaptchaKey={setCaptchaKey}/>
            }
            <div className="form-text text-danger">
              <small className="error-msg">{errorMsg}</small>
            </div>
            <button type="button" id="loginSubBtn" onClick={(e) =>
                Submit()}
                    className="btn btn-ssb btn-login btn-primary  mx-auto d-block mt-3">{t(
                `msg.loginForm.sky.login`)}
            </button>
          </form>
        </div>


        <div className="centered-breadcrumb">
          <div aria-label="breadcrumb">
            <ol className="breadcrumb">
              <li className="breadcrumb-item"><a onClick={() => {
                dispatch(modalActions.changeType({type: "join"}));
              }} href="#" className="text-decoration-none text-dark">{t(
                  `msg.loginForm.sky.findId`)}</a></li>
              <li className="breadcrumb-item"><Link
                  className="text-decoration-none text-dark">{t(
                  `msg.loginForm.sky.findPw`)}</Link></li>
              <li className="breadcrumb-item"><a href="#" onClick={() => {
                dispatch(modalActions.changeType({type: "join"}));}}
                  className="text-decoration-none text-dark">{t(`msg.join.sky.signup`)}</a></li>
            </ol>
            <div>

            </div>

          </div>
        </div>

      </div>
  );

}

function Captcha(props) {

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

  return <div className="card captchaCard">
    <div className="form-group card-body">
      <img className="captcha-img mb-3" id="imagePath"
           src={"./Nkey/open/image/" + props.imageName}/>
      <div className="input-group form-login form-cap">
        <input type="text" value={props.captcha} onChange={(e) => {
          props.setCaptcha(e.target.value)
        }}
               className="form-control captcha"/>
        <div className="input-group-append">
          <button type="button" className="btn captchaBtn"
                  id="captchaBtn" onClick={() => {
            captchaBtnClick();
          }}>
          </button>
        </div>
      </div>
    </div>
  </div>;
}

export default Login;

