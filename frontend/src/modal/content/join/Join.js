import "modal/css/join/join.css"
import "modal/css/base/secure.css"
import {Collapse} from "react-bootstrap";
import {useEffect, useRef, useState} from "react";
import {useTranslation} from "react-i18next";
import {
  GetInterval,
  PwSecureCheckFn,
  PwSecureLevel,
  Regex,
  StartCountdown
} from "utill/function";
import {
  DuplicateCheckApi
} from "utill/api/duplicateCheck/DuplicateCheckApi";
import {EmailApi} from "utill/api/email/EmailApi";
import {CodeCheckApi} from "utill/api/email/CodeCheckApi";
import {JoinApi} from "utill/api/join/JoinApi";
import {toast} from "react-toastify";

function Join(props) {
  const {t} = useTranslation();
  const [inputs, setInputs] = useState({
    userId: '',
    password: '',
    userName: '',
    email: '',
    authCode: ''
  });
  const variable = useRef({
    isDoubleClick: false // 더블 클릭 방지
  })
  const [errors, setErrors] = useState({
    userId: {message: '', error: false},
    password: {message: '', error: false},
    userName: {message: '', error: false},
    email: {message: '', error: false},
    authCode: {message: '', error: false},
    sbbAgreement: {message: '', error: false},
    infoAgreement: {message: '', error: false},
  });
  const [agree, setAgree] = useState({
    sbbAgreement: {open: false, check: false},
    infoAgreement: {open: false, check: false}
  })

  const [allAgreeCheck, setAllAgreeCheck] = useState(false)
  const [auth, setAuth] = useState({authToken: '', success: false})
  // 비밀번호 표시 변경 버튼 체크
  const [isShowPwChecked, setShowChecked] = useState(false);

  // Email Timer
  const [countDownTime, setCountDownTime] = useState(
      {message: '', error: false});
  const [authTimeLimit, setAuthTimeLimit] = useState(null);
  const [timer, setTimer] = useState(0);


  const passwordRef = useRef(null);
  const emailRef = useRef(null);

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
    const email = inputs.email;
    if (email === "") {
      changeError("email", t(`errorMsg.NotBlank`), true);
      return;
    }
    if (!Regex("email", email)) {
      return;
    }
    if (variable.current.isDoubleClick) {
      return;
    }
    variable.current.isDoubleClick = true;
    const response = await EmailApi("./email/join", {email: email});
    variable.current.isDoubleClick = false;
    if (response.code !== 200) {
      if (response.data.errorDetails !== undefined) {
        response.data.errorDetails.map((data) => {
          changeError("email", data.message, true);
        });
        return;
      }
      changeError("email", t(`errorMsg.server`), true);
    } else {
      changeError("email", '', false);
      changeError("authCode", '', false);
      setAuth({authToken: response.data.authToken, success: false})
      setTimer(await StartCountdown(response.data.authTimeLimit,
          response.data.authIssueTime));
      setAuthTimeLimit(await response.data.authTimeLimit);
    }

  }

  const authCodeCheckBtn = async () => {
    const authCode = inputs.authCode;
    console.log(authCode)
    if (authCode === "" || auth.authToken === "") {
      const message = authCode === "" ? t(`msg.userJoinForm.authCode.NotBlank`)
          : t(`errorMsg.error.authToken`);
      changeError("authCode", message, true);
      return;
    }
    if (variable.current.isDoubleClick) {
      return;
    }
    variable.current.isDoubleClick = true;

    const response = await CodeCheckApi(
        {authCode: authCode, authToken: auth.authToken});
    variable.current.isDoubleClick = false;
    if (response.code !== 200) {
      if (response.data.errorDetails) {
        response.data.errorDetails.map((data) => {
          changeError("authCode", data.message, true);
        });
        setCountDownTime({message: '', error: false});
        return;
      }
      changeError("authCode", t(`errorMsg.server`), true);
    } else {
      changeError("authCode", t(`msg.auth.success`), false);
      changeError("email", '', false);
      setTimer(0);
      setAuthTimeLimit(null);
      setCountDownTime({message: '', error: false});
      setAuth({...auth, success: true})
      emailRef.current.value = response.data.email;
    }
  }

  const handleShowPwChecked = async () => {
    const password = await passwordRef.current;

    await setShowChecked(!isShowPwChecked);
    password.type = !isShowPwChecked ? 'text' : 'password';
  }

  const clickShowAgreement = async (name) => {
    let copy = await name === "infoAgreement" ? agree.infoAgreement
        : agree.sbbAgreement;
    setAgree((agree) => {
      return {
        ...agree,
        [name]: {check: copy.check, open: !copy.open}
      }
    })
  }

  const clickAgreeChecked = async (e) => {
    e.stopPropagation();
    const {name} = e.target;
    setAllAgreeCheck(false);
    let copy = await name === "infoAgreement" ? agree.infoAgreement
        : agree.sbbAgreement;
    let copyCheck = !copy.check;
    changeError(name, '', !copyCheck)
    setAgree((agree) => {
      return {
        ...agree,
        [name]: {check: copyCheck, open: copy.open}
      }
    })
  }
  const clickAgreeAllChecked = async () => {
    let copyInfoAgree = agree.infoAgreement;
    let copySbbAgree = agree.sbbAgreement;
    let copyAllAgreeCheck = !allAgreeCheck;
    await setAllAgreeCheck(copyAllAgreeCheck);
    changeError("sbbAgreement", '', !copyAllAgreeCheck);
    changeError("infoAgreement", '', !copyAllAgreeCheck);
    setAgree(() => {
      return {
        sbbAgreement: {check: copyAllAgreeCheck, open: copySbbAgree.open},
        infoAgreement: {check: copyAllAgreeCheck, open: copyInfoAgree.open}
      }
    })
  }

  const duplicateCheck = async (name, value) => {
    const response = await DuplicateCheckApi(name, value);
    if (response !== undefined) {

      const isSuccess = response.code !== 200;
      if (isSuccess) {
        response.data.errorDetails.map((data) => {
          changeError(name, data.message, isSuccess);
        });
      } else {
        changeError(name, '', isSuccess);
      }

      return isSuccess;
    }

  }

  const RegexCheck = (name, input_value) => {

    let isRegex = !Regex(name, input_value);

    let message = Regex ? t(`msg.userJoinForm.` + name) : '';
    changeError(name, message, isRegex);
    return isRegex;
  }

  function changeError(name, message, error) {
    setErrors((errors) => {
      return {
        ...errors,
        [name]: {message: message, error: error}
      }
    });
  }

  const [secureLevel, setSecureLevel] = useState(
      {secLevelClass: '', secLevelStr: ''});
  const isStringBlank = (name) => {
    changeError(name, name === "authCode" ? t(
        `msg.userJoinForm.authCode.NotBlank`) : t(`errorMsg.NotBlank`), true);
    setSecureLevel(name === "password" ? {secLevelClass: '', secLevelStr: ''}
        : secureLevel);
    return true;
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
      return isStringBlank(name);
    }
    if (name === "userId" || name === "email" || name === "userName") {
      const isRegex = RegexCheck(name, input_value);
      // 정규표현식이 맞고 빈칸이 아닌 경우
      if (!isRegex && name !== "email") { // 중복 체크
        await duplicateCheck(name, input_value);
      }
      // 중복 체크 후 중복이면 리턴
    }
    if (name === "password") {
      // 8글자이하 16글자 초과시에
      let number = await PwSecureLevel(input_value);
      setSecureLevel(PwSecureCheckFn(number));
      let error = number === 0;
      let message = error ? t(`msg.userJoinForm.password`) : '';
      changeError(name, message, error);
    }
  }
  const submitHandler = async (e) => {
    e.stopPropagation();

    // 이용 약관 동의 확인
    for (const [key,value] of Object.entries(agree)) {
      if (!value.check) {
        changeError(key, t(`errorMsg.error.`+key) , !value.check);
      }
    }

    // error 여부 확인
    for (const [key, value] of Object.entries(errors)) {
      if (value.error) {
        return;
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
      return;
    }
    // 이메일 인증 여부 확인
    if (!auth.success) {
      changeError("authCode", t(`msg.userJoinForm.email2`), !auth.success);
      return;
    }

    if (variable.current.isDoubleClick) {
      return;
    }
    variable.current.isDoubleClick = true;

    let loading = toast.loading("회원가입 진행중...");
    const body = {
      userId:inputs.userId,
      password:inputs.password,
      userName:inputs.userName,
      email:inputs.email,
      authCode:inputs.authCode,
      sbbAgreement:agree.sbbAgreement.check,
      infoAgreement:agree.infoAgreement.check}
    const response = await JoinApi(body);
    const result = response.code !== 200;
    if (result) {
      response.data.errorDetails.map((data) => {
        const hasField = data.field === "email" || data.field === "authCode";
        if (hasField) {
          setAuth({...auth, success: false});
          changeError("email", data.message, true);
          changeError("authCode", data.message, true);
        } else {
          changeError(data.field, data.message, true);
        }
      });
      toast.dismiss(loading);
    } else {
      toast.dismiss(loading);
      toast.success("회원가입이 완료되었습니다.");
      props.setModalVisible(false);
    }

    variable.current.isDoubleClick = false;
  }

  return (
      <div className="card">
        <div className="card-body">
          <h5 className="card-title text-center">{t(
              `msg.join.sky.signup`)}</h5>
          <form>
            <div className="form-group">
              <div className={"input-group form-join form-id "
                  + (errors.userId.error ? 'error' : 'on')}>
                <input name="userId" id="userId" type="text" placeholder={t(`msg.common.sky.id`)}
                       className={"form-control " + (errors.userId.error
                           ? 'border-danger' : '')} onKeyUp={onKeyUp}/>
              </div>
              <div className="form-text text-danger">
                <small id="id-NotThyme-msg">{errors.userId.error
                    ? errors.userId.message : ''}</small>
              </div>
            </div>
            <div className="form-group">
              <div className={"input-group form-join form-pw "
                  + (errors.password.error ? 'error' : 'on')}>
                <input type="password" name="password" id="password"
                       placeholder={t(`msg.common.sky.pw`)}
                       className={"form-control " + (errors.password.error
                           ? 'border-danger' : '')}
                       onKeyUp={onKeyUp} ref={passwordRef}/>
                <div className="password-info">
                  <em className={"how-secure " + secureLevel.secLevelClass}
                      name="secureLevel"
                      id="secureLevel">{secureLevel.secLevelStr}</em>
                  <button type="button" id="btn-show"
                          className={"btn-show hide " + (isShowPwChecked ? 'on'
                              : '')} onClick={handleShowPwChecked}>
                    <span className="blind"></span>
                  </button>
                </div>
              </div>
              <div className="form-text text-danger">
                <small id="password-NotThyme-msg"> {errors.password.error
                    ? errors.password.message : ''}</small>
              </div>
            </div>
            <div className="form-group">
              <div className={"input-group form-join form-name "
                  + (errors.userName.error ? 'error' : 'on')}>
                <input className={"form-control " + (errors.userName.error
                    ? 'border-danger' : '')}
                       name="userName" id="userName"
                       placeholder={t(`msg.common.sky.userName`)}
                       onKeyUp={onKeyUp}/>
              </div>
              <div className="form-text text-danger">
                <small id="userName-NotThyme-msg"> {errors.userName.error
                    ? errors.userName.message : ''}</small>
              </div>
            </div>
            <div className="form-group">
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
                          id="sendCodeButton">인증번호 전송
                  </button>
                </div>
              </div>
              <div className="form-text text-danger">
                <small id="email-NotThyme-msg">{errors.email.error
                    ? errors.email.message : ''}</small>
              </div>
            </div>
            <div className="form-group">
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
                          id="verifyCodeButton">인증번호 확인
                  </button>
                </div>
              </div>
              <small id="verification-msg"
                     className={errors.authCode.error ? 'text-danger'
                         : ''}>{errors.authCode.message}</small>
              <small id="verification-time"
                     className={countDownTime.error ? 'text-danger'
                         : ''}>{countDownTime.message}</small>
            </div>

            <div className="form-group"
                 aria-controls="example-collapse-text"
                 onClick={() => clickShowAgreement("sbbAgreement")}
                 aria-expanded={agree.sbbAgreement.open}>
              <input type="checkbox" name="sbbAgreement" id="sbbAgreement"
                     className="form-check-input agreeCheck me-1"
                     checked={agree.sbbAgreement.check}
                     onClick={clickAgreeChecked} readOnly/>
              <label className="mb-2">
                <small className="mainColor me-1">{t(
                    `msg.joinAgree.sky.required`)}</small>
                <span className="form-text ">{t(`msg.common.sky.logo`)+ " " + t(`msg.joinAgree.sky.agree1`)}</span>
                <div className="form-text text-danger">
                  <small id="infoAgree-NotThyme-msg">{errors.sbbAgreement.message}</small>
                </div>
              </label>
              <Collapse in={agree.sbbAgreement.open}>
                <div id="example-collapse-text"
                     className="border overflow-auto border-gray form-text">
                  네이버 서비스 이용계약이 해지되면, 관련 법령 및 개인정보처리방침에 따라 네이버가 해당 회원의 정보를 보유할
                  수
                  있는 경우를 제외하고,
                  해당 회원 계정에 부속된 게시물 일체를 포함한 회원의
                  모든 데이터는 소멸됨과 동시에 복구할 수 없게 됩니다. 다만, 이 경우에도 다른 회원이 별도로 담아갔거나
                  스크랩한 게시물과 공용 게시판에 등록한 댓글 등의 게시물은 삭제되지 않으므로 반드시
                </div>
              </Collapse>
            </div>
            <div className="form-group"
                 aria-controls="example-collapse-text2"
                 onClick={() => clickShowAgreement("infoAgreement")}
                 aria-expanded={agree.infoAgreement.open}>
              <input type="checkbox" name="infoAgreement" id="infoAgreement"
                     className="form-check-input agreeCheck me-1"
                     checked={agree.infoAgreement.check}
                     onClick={clickAgreeChecked} readOnly/>
              <label className="mb-2">
                <small className="mainColor me-1">{t(
                    `msg.joinAgree.sky.required`)}</small>
                <span className="form-text">{t(
                    `msg.joinAgree.sky.agree2`)}</span>
              <div className="form-text text-danger">
                <small id="infoAgree-NotThyme-msg">{errors.infoAgreement.message}</small>
              </div>
              </label>
              <Collapse in={agree.infoAgreement.open}>
                <div id="example-collapse-text2"
                     className="border overflow-auto border-gray form-text">
                  네이버 서비스 이용계약이 해지되면, 관련 법령 및 개인정보처리방침에 따라 네이버가 해당 회원의 정보를 보유할
                  수
                  있는 경우를 제외하고,
                  해당 회원 계정에 부속된 게시물 일체를 포함한 회원의
                  모든 데이터는 소멸됨과 동시에 복구할 수 없게 됩니다. 다만, 이 경우에도 다른 회원이 별도로 담아갔거나
                  스크랩한 게시물과 공용 게시판에 등록한 댓글 등의 게시물은 삭제되지 않으므로 반드시
                  해지 신청 이전에 삭제하신 후 탈퇴하시기 바랍니다.
                </div>
              </Collapse>

            </div>

            <div className="form-group">
              <input type="checkbox" name="allAgreeCheck" id="allAgreeCheck"
                     className="form-check-input agreeCheck me-1"
                     checked={allAgreeCheck}
                     onClick={clickAgreeAllChecked} readOnly/>
              <label className="mb-1">
                <span className="form-text">{t(
                    `msg.joinAgree.sky.agree.all`)}</span>
              </label>
            </div>
            <div className="d-grid gap-2 col-6 mx-auto">
              <button type="button" id="subBtn" onClick={submitHandler}
                      className="btn btn-primary btn-block btn-dark mt-5">가입하기
              </button>
            </div>
          </form>
        </div>
      </div>
  )
}

export default Join;