import "modal/css/join/join.css"
import "modal/css/base/secure.css"
import {Collapse} from "react-bootstrap";
import {useEffect, useRef, useState} from "react";
import {useTranslation} from "react-i18next";
import {
  ChangeError,
  GetInterval,
  PwSecureCheckFn,
  PwSecureLevel,
} from "utill/function";
import {
  DuplicateCheckApi
} from "utill/api/duplicateCheck/DuplicateCheckApi";
import {JoinApi} from "utill/api/join/JoinApi";
import {toast} from "react-toastify";
import {EmailInput} from "components/input/EmailInput";
import {AuthInput} from "components/input/AuthInput";
import {Input} from "components/input/Input";
import {PwInput} from "components/input/PwInput";
import {modalActions} from "store/modalType/modalType";
import {useDispatch} from "react-redux";
import {BtnOutLine} from "components/ button/BtnOutLine";
import {LOGIN} from "modal/content/ModalContent";
import {EMAIL_JOIN} from "utill/api/ApiEndpoints";

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
  const dispatch = useDispatch();
  // authToken
  const [auth, setAuth] = useState({authToken: '', success: false})
  // 비밀번호 표시 변경 버튼 체크
  const [isShowPwChecked, setShowChecked] = useState(false);
  // Email Timer
  const [countDownTime, setCountDownTime] = useState(
      {message: '', error: false});
  const [authTimeLimit, setAuthTimeLimit] = useState(null);
  const [timer, setTimer] = useState(0);
  const emailRef = useRef(null);

  const passwordRef = useRef(null);

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
    const body = {email: inputs.email, sendType: props.type};
    await props.ClickBtnSendCode(
        EMAIL_JOIN,
      inputs, t, setErrors, variable, body, setAuth, setTimer,
      setAuthTimeLimit)
  }
  const authCodeCheckBtn = async () => {
    props.ClickBtnAuthCodeCheck(setInputs, inputs, auth, t, setErrors, variable,
        setCountDownTime,
        setTimer, setAuthTimeLimit, setAuth, emailRef, props.type);
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
    ChangeError(setErrors, name, '', !copyCheck)
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
    ChangeError(setErrors, "sbbAgreement", '', !copyAllAgreeCheck);
    ChangeError(setErrors, "infoAgreement", '', !copyAllAgreeCheck);
    setAgree(() => {
      return {
        sbbAgreement: {check: copyAllAgreeCheck, open: copySbbAgree.open},
        infoAgreement: {check: copyAllAgreeCheck, open: copyInfoAgree.open}
      }
    })
  }
  const duplicateCheck = async (name, value) => {
    const response = await DuplicateCheckApi(name, value);
    const isSuccess = response.code !== 200;
    if (isSuccess) {
      if (response.data.errorDetails !== undefined) {
        response.data.errorDetails.map((data) => {
          ChangeError(setErrors, name, data.message, isSuccess);
        });
        return;
      }
      ChangeError(setErrors, name, t(`errorMsg.server`), isSuccess);
    } else {
      ChangeError(setErrors, name, '', isSuccess);
    }

    return isSuccess;

  }

  const [secureLevel, setSecureLevel] = useState(
      {secLevelClass: '', secLevelStr: ''});
  const isStringBlank = (name) => {
    ChangeError(setErrors, name, name === "authCode" ? t(
        `msg.userJoinForm.authCode.NotBlank`) : t(`errorMsg.NotBlank`), true);
    setSecureLevel(name === "password" ? {secLevelClass: '', secLevelStr: ''}
        : secureLevel);
    return true;
  }
  const onKeyUp = async (e) => {
    const {value, name} = e.target;
    const input_value = value.split(" ").join("");
    console.log("name:"+name)
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
      const isRegex = props.RegexCheck(name, input_value, setErrors, t);
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
      ChangeError(setErrors, name, message, error);
    }
  }

  // 회원가입
  const submitHandler = async (e) => {
    e.stopPropagation();

    // 이용 약관 동의 확인
    for (const [key, value] of Object.entries(agree)) {
      if (!value.check) {
        ChangeError(setErrors, key, t(`errorMsg.error.` + key), !value.check);
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
      ChangeError(setErrors, "authCode", t(`msg.userJoinForm.email2`),
          !auth.success);
      return;
    }

    if (variable.current.isDoubleClick) {
      return;
    }
    variable.current.isDoubleClick = true;

    let loading = toast.loading(t(`msg.join.sky.singup.process`));
    const body = {
      userId: inputs.userId,
      password: inputs.password,
      userName: inputs.userName,
      email: inputs.email,
      authCode: inputs.authCode,
      sbbAgreement: agree.sbbAgreement.check,
      infoAgreement: agree.infoAgreement.check
    }
    const response = await JoinApi(body);
    const result = response.code !== 200;
    if (result) {
      response.data.errorDetails.map((data) => {
        const hasField = data.field === "email" || data.field === "authCode";
        if (hasField) {
          setAuth({...auth, success: false});
          ChangeError(setErrors, "email", data.message, true);
          ChangeError(setErrors, "authCode", data.message, true);
        } else {
          ChangeError(setErrors, data.field, data.message, true);
        }
      });
      toast.dismiss(loading);
    } else {
      toast.dismiss(loading);
      toast.success("회원가입이 완료되었습니다.");
      dispatch(modalActions.changeType({type: "LOGIN"}));
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
              <Input name="userId" placeholder={t(`msg.common.sky.id`)} type="text"
                     error={errors.userId.error} message={errors.userId.message}
                     iconClass="form-id" onKeyUp={onKeyUp} t={t}/>
            </div>
            <div className="form-group">
              <PwInput handleShowPwChecked={handleShowPwChecked} name="password"
                       error={errors.password.error}
                       message={errors.password.message}
                       isShowPwChecked={isShowPwChecked} onKeyUp={onKeyUp}
                       passwordRef={passwordRef}
                       placeholder={t(`msg.common.sky.pw`)}
                       secLevelStr={secureLevel.secLevelStr}
                       secLevelClass={secureLevel.secLevelClass}/>
            </div>
            <div className="form-group">
              <Input name="userName" placeholder={t(`msg.common.sky.userName`)} type="text"
                     error={errors.userName.error}
                     message={errors.userName.message} iconClass="form-name"
                     onKeyUp={onKeyUp} t={t}/>
            </div>
            <div className="form-group">
              {EmailInput(errors, auth, t, onKeyUp, emailRef, clickBtnSendCode)}
            </div>
            <div className="form-group">
              {AuthInput(errors, auth, t, onKeyUp, authCodeCheckBtn,
                  countDownTime)}
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
                <span className="form-text ">{t(`msg.common.sky.logo`) + " "
                    + t(`msg.joinAgree.sky.agree1`)}</span>
                <div className="form-text text-danger">
                  <small
                      id="infoAgree-NotThyme-msg">{errors.sbbAgreement.message}</small>
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
                  <small
                      id="infoAgree-NotThyme-msg">{errors.infoAgreement.message}</small>
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
                      className="btn btn-primary btn-block btn-dark mt-3">가입하기
              </button>
              <BtnOutLine id="cancelBtn" text={t(
                  `msg.common.sky.cancel`)} event={() => dispatch(modalActions.changeType({type: LOGIN}))} />
            </div>
          </form>
        </div>
      </div>
  )
}

export default Join;