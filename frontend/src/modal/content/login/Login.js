import {useEffect, useRef, useState} from "react";
import {useTranslation} from "react-i18next";
import {useDispatch} from "react-redux";
import {toast} from "react-toastify";
import "modal/css/Login/login.css"
import {authActions} from "store/auth/authReducers";
import {useNavigate} from "react-router";
import LoginApi from "utill/api/LoginApi/LoginApi";
import {modalActions} from "store/modalType/modalType";
import {helpActions} from "store/helpType/helpType";
import Captcha from "components/captcha/Captcha";

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

    if (userId === "") {
      setErrorMsg(t(`msg.userId.NotBlank`));
      return;
    }
    if (password === "") {
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
      props.bc.postMessage({type: "login"})
      props.closeModal();
      navigate("/");

    }).catch(error => {
      let message;
      toast.dismiss(loading);
      if (error.response.status === 401) {
        message = error.response.data.message;
        setCaptchaKey(error.response.data.captchaKey);
        setImageName(error.response.data.imageName);
      } else if (error.response.status !== 500) {
        let errorDetail = error.response.data.errorDetails[0];
        message = errorDetail.message;
      } else {
        message = t(`errorMsg.server`);
      }
      setCaptcha('');
      toast.error(message)
      setErrorMsg(message);
    }).finally(() => {
      variable.current.isDoubleClick = false;
    });
  }
  return (
      <div className="card mainCard">
        <div className="card-body">
          <h4 className="card-title logo-text mainLogo">{t(
              `msg.common.sky.logo`)}</h4>
          <form className="mt-4" method="post">
            <div className="form-group">
              <div className="input-group form-login form-id">
                <input onKeyUp={(e) => {
                  let value = e.target.value;
                  setUserId(value);
                }} type="text" id="userId" name="userId"
                       className="form-control"
                       placeholder={t(`msg.common.sky.id`)}/>
              </div>
            </div>

            <div className="form-group">
              <div className="input-group form-login form-pw">
                <input type="password"
                       onKeyUp={(e) => {
                         let value = e.target.value;
                         setPassword(value);
                       }} className=" form-control "
                       placeholder={t(`msg.common.sky.pw`)}/>
              </div>
            </div>
            {
                captchaKey != null && <Captcha imageName={imageName}
                                               setImageName={setImageName} t={t}
                                               captcha={captcha}
                                               placeholder={t(
                                                   `msg.common.sky.captcha`)}
                                               setCaptcha={setCaptcha}
                                               setCaptchaKey={setCaptchaKey}
                                               type="LOGIN"/>
            }
            <div className="form-text text-danger">
              <small className="error-msg">{errorMsg}</small>
            </div>
            <div className="d-grid gap-2 col-6 mx-auto">
              <button type="button" id="loginSubBtn" onClick={() =>
                  Submit()}
                      className="btn btn-primary btn-block btn-dark mt-3">{t(
                  `msg.loginForm.sky.login`)}
              </button>
            </div>
          </form>
        </div>


        <div className="centered-breadcrumb">
          <div aria-label="breadcrumb">
            <ol className="breadcrumb">
              <li className="breadcrumb-item"><a onClick={() => {
                dispatch(modalActions.changeType({type: "HELP"}));
                dispatch(helpActions.changeType({helpType: "ID"}));
              }} href="#" className="text-decoration-none text-dark">{t(
                  `msg.loginForm.sky.findId`)}</a></li>
              <li className="breadcrumb-item"><a href="#" onClick={() => {
                dispatch(modalActions.changeType({type: "HELP"}));
                dispatch(helpActions.changeType({helpType: "PW"}));
              }}
                                                 className="text-decoration-none text-dark">{t(
                  `msg.loginForm.sky.findPw`)}</a></li>
              <li className="breadcrumb-item"><a href="#" onClick={() => {
                dispatch(modalActions.changeType({type: "JOIN"}));
              }}
                                                 className="text-decoration-none text-dark">{t(
                  `msg.join.sky.signup`)}</a></li>
            </ol>
            <div>

            </div>

          </div>
        </div>
      </div>
  );

}

export default Login;

