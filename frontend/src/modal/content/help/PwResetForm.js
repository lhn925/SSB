import {useRef, useState} from "react";
import {PwInput} from "components/input/PwInput";
import {Input} from "components/input/Input";
import Captcha from "components/captcha/Captcha";
import {
  ChangeError,
  PwSecureCheckFn,
  PwSecureLevel
} from "utill/function";
import {ResetApi} from "utill/api/help/ResetApi";
import {toast} from "react-toastify";
import {useNavigate} from "react-router";

export default function PwResetForm(props) {
  const [inputs, setInputs] = useState({
    newPw: '',
    newPwChk: '',
    captcha: ''
  });
  const [errors, setErrors] = useState({
    newPw: {message: '', error: false},
    newPwChk: {message: '', error: false},
    captcha: {message: '', error: false},
  });

  const variable = useRef({
    isDoubleClick: false // 더블 클릭 방지
  })
  const navigate = useNavigate();
  const [inputType, setInputType] = useState("password");

  const [captchaKey, setCaptchaKey] = useState(props.captchaKey);
  const [imageName, setImageName] = useState(props.imageName);
  const [secureLevel, setSecureLevel] = useState(
      {secLevelClass: '', secLevelStr: ''});
  // 비밀번호 표시 변경 버튼 체크
  const [isShowPwChecked, setShowChecked] = useState(false);
  const newPwRef = useRef(null);
  const handleShowPwChecked = async () => {
    const password = await newPwRef.current;

    const type = !isShowPwChecked ? 'text' : 'password';
    await setShowChecked(!isShowPwChecked);
    password.type = type;
    setInputType(type);
  }

  const pwMatch = (input_value, name) => {
    if (inputs.newPw === "") {
      ChangeError(setErrors, "newPw", props.t(`msg.newPw.NotBlank`), true)
    }
    const value = name === "newPW" ? inputs.newPwChk : inputs.newPw;
    return input_value === value;
  }

  const onKeyUp = async (e) => {
    const {value, name} = e.target;
    const input_value = value.split(" ").join("");

    setInputs((inputs) => {
      return {
        ...inputs,
        [name]: input_value
      }
    })

    const isBlank = input_value === "";
    let message = isBlank ? props.t(`msg.` + name + `.NotBlank`) : '';
    ChangeError(setErrors, name, message, isBlank);
    if (isBlank) {
      setSecureLevel(name === "newPw" ? {secLevelClass: '', secLevelStr: ''}
          : secureLevel);
      return;
    }
    if (name === "newPw") {
      let number = await PwSecureLevel(input_value);
      setSecureLevel(PwSecureCheckFn(number));
      let error = number === 0;
      let message = error ? props.t(`msg.userJoinForm.password`) : '';
      ChangeError(setErrors, name, message, error);
    }
    if (name !== "captcha") {
      const isMatch = pwMatch(input_value, name);
      const message = !isMatch ? props.t(`errorMsg.pw.mismatch`) : '';
      ChangeError(setErrors, "newPwChk", message,
          !isMatch)
    }
  }

  const isStringBlank = (name) => {
    ChangeError(setErrors, name, props.t(`msg.` + name + `.NotBlank`), true)
    setSecureLevel(name === "newPw" ? {secLevelClass: '', secLevelStr: ''}
        : secureLevel);
    return true;
  }
  const clickBtnPwChange = async () => {
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

    let isMatch = pwMatch(inputs.newPw, "newPw");
    let message = isMatch ? '' : props.t(`errorMsg.pw.mismatch`)
    ChangeError(setErrors, "newPwChk", message, !isMatch);
    if (!isMatch) {
      return;
    }

    if (variable.current.isDoubleClick) {
      return;
    }
    variable.current.isDoubleClick = true;

    let loading = toast.loading("비밀번호 변경 중...");

    const body = {
      userId: props.userId,
      captcha: inputs.captcha,
      imageName: imageName,
      captchaKey: captchaKey,
      newPw: inputs.newPw,
      newPwChk: inputs.newPwChk
    }
    const response = await ResetApi(body);
    variable.current.isDoubleClick = false;
    toast.dismiss(loading);
    if (response.code === 200) {
      props.closeModal();
      alert(props.t(`msg.help.pw.change`));
      navigate('/');
      return;
    }
    const error400 = response.code === 400;
    const errorDetail = response.data.errorDetails !== undefined
        ? response.data.errorDetails : null;
    if (errorDetail !== undefined && error400) {
      errorDetail.map((data) => {
        if (data.field === "imageName") {
          setImageName(data.rejectValue)
        } else if (data.field === "captchaKey") {
          setCaptchaKey(data.rejectValue)
        } else {
          ChangeError(setErrors, data.field, data.message, true);
        }
      })
      return;
    }
    props.closeModal();
    toast.error(props.t(`errorMsg.error.token`));
    navigate('/');
  }
  return (
      <div className="card mainCard">
        <div className="card-body">
          <h4 className="card-title logo-text mainLogo">{props.t(
              `msg.common.sky.logo`)}</h4>
          <form className="mt-4" method="post">
            <div className="form-group">
              <PwInput handleShowPwChecked={handleShowPwChecked} name="newPw"
                       error={errors.newPw.error}
                       message={errors.newPw.message}
                       isShowPwChecked={isShowPwChecked} onKeyUp={onKeyUp}
                       passwordRef={newPwRef}
                       placeholder={props.t(`msg.common.sky.newPw`)}
                       secLevelStr={secureLevel.secLevelStr}
                       secLevelClass={secureLevel.secLevelClass}/>
            </div>

            <div className="form-group">
              <Input
                  type={inputType} onKeyUp={onKeyUp} name="newPwChk"
                  error={errors.newPwChk.error}
                  message={errors.newPwChk.message}
                  placeholder={props.t(`msg.common.sky.newPwChk`)}
                  iconClass="form-pw"
              />
            </div>
            <Captcha imageName={imageName}
                     setImageName={setImageName} t={props.t}
                     placeholder={props.t(`msg.common.sky.captcha`)}
                     setCaptchaKey={setCaptchaKey}
                     error={errors.captcha.error}
                     message={errors.captcha.message}
                     type="Help"
                     onKeyUp={onKeyUp}
            />
            <div className="d-grid gap-2 col-6 mx-auto">
            <button type="button" id="changeSubBtn" onClick={clickBtnPwChange}
                    className="btn btn-primary btn-block btn-dark mt-3">
              {props.t(`msg.common.sky.pwReset`)}
            </button>
            </div>
          </form>
        </div>
      </div>
  );
}

