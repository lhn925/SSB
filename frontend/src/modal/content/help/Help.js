import "modal/css/help/help.css"
import "modal/css/join/join.css"
import "modal/css/base/secure.css"
import {useTranslation} from "react-i18next";
import {useEffect, useRef, useState} from "react";
import {
  ChangeError,
  GetInterval,
} from "utill/function";
import {FindIdApi} from "utill/api/help/FindIdApi";
import {useDispatch} from "react-redux";
import EmailAuthForm from "modal/content/help/EmailAuthForm";
import Show from "modal/content/help/Show";
import IdQuery from "modal/content/help/IdQuery";
import ResetFormApi from "utill/api/help/ResetFormApi";
import PwResetForm from "./PwResetForm";

function Help(props) {
  const {t} = useTranslation();
  const [inputs, setInputs] = useState({
    email: '',
    authCode: '',
  });
  const [errors, setErrors] = useState({
    email: {message: '', error: false},
    authCode: {message: '', error: false},
  });
  const dispatch = useDispatch();
  const [findUserId, setFindUserId] = useState({
    userId: null,
    createdDateTime: null
  });

  const [userPwResetForm, setUserPwRestForm] = useState({
    userId:null,
    captcha:null,
    captchaKey:null,
    imageName:null
  });
  // authToken
  const [auth, setAuth] = useState({authToken: '', success: false})
  // Email Timer
  const [countDownTime, setCountDownTime] = useState(
      {message: '', error: false});
  const [authTimeLimit, setAuthTimeLimit] = useState(null);
  const [timer, setTimer] = useState(0);
  const emailRef = useRef(null);
  const variable = useRef({
    isDoubleClick: false // 더블 클릭 방지
  })
  useEffect(() => {
    let countDownInterVal = null;
    if (timer > 0) {

      countDownInterVal = GetInterval(timer, setTimer, authTimeLimit,
          setCountDownTime, t(`msg.auth.timeOut`))

    }
    return () => {
      clearInterval(countDownInterVal);
    }
  }, [timer, authTimeLimit])

  const clickBtnSendCode = async () => {

    const body = {
      email: inputs.email,
      helpType: props.helpType,
      sendType: props.helpType
    };
    if (props.helpType === "PW") {
      body.userId = props.userId;
    }
    const url = "./email/find";
    await props.ClickBtnSendCode(url,
        inputs, t, setErrors, variable, body, setAuth, setTimer,
        setAuthTimeLimit
    )
  }
  const clickBtnAuthCodeCheck = async () => {
    await props.ClickBtnAuthCodeCheck(setInputs,inputs, auth, t, setErrors, variable,
        setCountDownTime,
        setTimer, setAuthTimeLimit, setAuth, emailRef, props.helpType);
  }

  const clickBtnResetFormChange = async () => {
    const userId = props.userId;
    const authToken = auth.authToken;

    for (const [key, value] of Object.entries(errors)) {
      if (value.error) {
        return true;
      }
    }
    // 공백 여부 확인
    let isBlank = false;
    for (const [key, value] of Object.entries(inputs)) {
      if (value === "") {
        isBlank = isStringBlank(key)
      }
    }
    if (isBlank) {
      return true;
    }
    // 이메일 인증 여부 확인
    if (!auth.success) {
      ChangeError(setErrors, "authCode", t(`msg.userJoinForm.email2`),
          !auth.success);
      return;
    }
    if (variable.current.isDoubleClick) {
      return;
    }
    variable.current.isDoubleClick = true;
    const response = await ResetFormApi(userId, authToken,inputs.email);
    variable.current.isDoubleClick = false;
    if (response.code != 200) {
      setAuth((auth) => {
        return {
          ...auth,
          success: false,
          authToken: ''
        }
      })
      if (response.data.errorDetails !== undefined) {
        response.data.errorDetails.map((data) => {
          ChangeError(setErrors, data.field, data.message, true);
        })
        return;
      }
      ChangeError(setErrors, "userId", t(`errorMsg.server`), true);
    } else {
      setUserPwRestForm((reset)=>{
        return {
          ...reset,
          userId: response.data.userId,
          captchaKey: response.data.captchaKey,
          captcha: response.data.captcha,
          imageName: response.data.imageName
        }
      })
    }
  }
  const isStringBlank = (name) => {
    ChangeError(setErrors, name,
        name === "authCode" ? t(`msg.userJoinForm.authCode.NotBlank`) : t(
            `errorMsg.NotBlank`), true);
    return true;
  }
  const clickBtnFindShowId = async () => {


    // error 여부 확인
    for (const [key, value] of Object.entries(errors)) {
      if (value.error) {
        return true;
      }
    }
    // 공백 여부 확인
    let isBlank = false;
    for (const [key, value] of Object.entries(inputs)) {
      if (value === "") {
        isBlank = isStringBlank(key)
      }
    }
    if (isBlank) {
      return true;
    }
    // 이메일 인증 여부 확인
    if (!auth.success) {
      ChangeError(setErrors, "authCode", t(`msg.userJoinForm.email2`),
          !auth.success);
      return;
    }

    if (variable.current.isDoubleClick) {
      return;
    }
    variable.current.isDoubleClick = true;

    const response = await FindIdApi(inputs.email, auth.authToken);

    if (response.code !== 200) {
      response.data.errorDetails.map((data) => {
        ChangeError(setErrors, data.field, data.message, true);
      });
      setAuth({success: false, authToken: ""})
    } else {
      await setFindUserId({
        userId: response.data.userId,
        createdDateTime: response.data.createdDateTime
      });
    }
    variable.current.isDoubleClick = false;
  }

  const onKeyUp = async (e) => {
    const {value, name} = e.target;
    const input_value = value.split(" ").join("");
    // 공백 체크
    setInputs((inputs) => {
      return {
        ...inputs,
        [name]: input_value
      }
    });
    if (input_value === "") {
      ChangeError(setErrors, name,
          name === "authCode" ? t(`msg.userJoinForm.authCode.NotBlank`) : t(
              `errorMsg.NotBlank`), true);
    }
    if (name === "email") {
      props.RegexCheck(name, input_value, setErrors, t);
    }
  }

  if (props.helpType === "PW") {
    const enEmail = props.enEmail;
    return (
        <>
          {
            enEmail=== undefined ? <IdQuery RegexCheck={props.RegexCheck}
                                             ClickBtnSendCode={props.ClickBtnSendCode}
                                             ClickBtnAuthCodeCheck={props.ClickBtnAuthCodeCheck}
                                             closeModal={props.closeModal}
                                             helpType={props.helpType}/> :
                auth.success && userPwResetForm.captchaKey != null ? <PwResetForm
                    closeModal={props.closeModal}
                    userId={userPwResetForm.userId}
                   imageName={userPwResetForm.imageName} captchaKey={userPwResetForm.captchaKey} t={t}/> :
                    <EmailAuthForm t={t} errors={errors} dispatch={dispatch}
                                                   auth={auth} onKeyUp={onKeyUp}
                                                   emailRef={emailRef}
                                                   clickBtnSendCode={clickBtnSendCode}
                                                   clickBtnAuthCodeCheck={clickBtnAuthCodeCheck}
                                                   clickBtnEvent={clickBtnResetFormChange}
                                                   countDownTime={countDownTime}
                                                   enEmail={enEmail}
                                                   helpType={props.helpType}/>
          }
        </>
    )
  } else {
    return (
        <>
          {
            auth.success && findUserId.userId !== null ?
                <Show t={t} dispatch={dispatch} findUserId={findUserId}/> :
                <EmailAuthForm t={t} errors={errors}
                               auth={auth} onKeyUp={onKeyUp}
                               emailRef={emailRef}
                               dispatch={dispatch}
                               clickBtnSendCode={clickBtnSendCode}
                               clickBtnAuthCodeCheck={clickBtnAuthCodeCheck}
                               clickBtnEvent={clickBtnFindShowId}
                               countDownTime={countDownTime}
                               helpType={props.helpType}/>
          }
        </>
    )
  }

}

export default Help;