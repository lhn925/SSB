import Login from "modal/content/login/Login";
import Join from "modal/content/join/Join";
import Help from "modal/content/help/Help";
import {
  AuthCodeCheck,
  ChangeError,
  Regex,
  SendCode
} from "utill/function";
import IdQuery from "modal/content/help/IdQuery";
import {useSelector} from "react-redux";
import Modal from "modal/Modal";

function ModalContent(props) {
  const type = useSelector(state => state.modalType.type);
  const helpType = useSelector(state => state.helpType.helpType);
  return (
      <>
        {
            props.modalVisible && <Modal visible={props.modalVisible}
                                   closable={true}
                                   maskClosable={false}
                                   onClose={props.closeModal}>
              <Content bc={props.bc} closeModal={props.closeModal} type={type} helpType={helpType}/>
            </Modal>
        }
      </>
  )
}

function Content(props) {
  if (props.type === "JOIN") {
    return (
        <>
          <Join RegexCheck={RegexCheck} ClickBtnSendCode={ClickBtnSendCode}
                ClickBtnAuthCodeCheck={ClickBtnAuthCodeCheck}
                closeModal={props.closeModal} type={props.type}/>
        </>
    );
  } else if (props.type === "HELP" && props.helpType === "ID") {
    return (
        <>
          <Help RegexCheck={RegexCheck} ClickBtnSendCode={ClickBtnSendCode}
                ClickBtnAuthCodeCheck={ClickBtnAuthCodeCheck}
                closeModal={props.closeModal}
                helpType={props.helpType}/>
        </>
    );
  } else if (props.type === "HELP" && props.helpType === "PW") {
    return (
        <>
          <IdQuery RegexCheck={RegexCheck}
                   ClickBtnSendCode={ClickBtnSendCode}
                   ClickBtnAuthCodeCheck={ClickBtnAuthCodeCheck}
                   closeModal={props.closeModal}
                   helpType={props.helpType}/>
        </>
    );
  } else {
    return (
        <>
          <Login bc={props.bc} closeModal={props.closeModal}/>
        </>
    );
  }
}


function RegexCheck (name, input_value, setErrors, t)  {
  let isRegex = !Regex(name, input_value);
  let message = Regex ? t(`msg.userJoinForm.` + name) : '';
  ChangeError(setErrors, name, message, isRegex);
  return isRegex;
}

async function ClickBtnSendCode(url, inputs, t, setErrors, variable, body,
    setAuth, setTimer, setAuthTimeLimit) {
  let email = inputs.email;
  if (email === "") {
    ChangeError(setErrors, "email", t(`errorMsg.NotBlank`), true);
    return;
  }
  if (!Regex("email", email)) {
    return;
  }
  if (variable.current.isDoubleClick) {
    return;
  }
  variable.current.isDoubleClick = true;
  await SendCode(url,
      body,
      setErrors, setAuth, setTimer,
      setAuthTimeLimit,
      t(`errorMsg.server`));
  variable.current.isDoubleClick = false;
}

async function ClickBtnAuthCodeCheck(setInputs, inputs, auth, t, setErrors,
    variable,
    setCountDownTime,
    setTimer, setAuthTimeLimit, setAuth, emailRef, type) {
  const authCode = inputs.authCode;
  if (authCode === "" || auth.authToken === "") {
    const message = authCode === "" ? t(`msg.userJoinForm.authCode.NotBlank`)
        : t(`errorMsg.error.authToken`);
    ChangeError(setErrors, "authCode", message, true);
    return;
  }
  if (variable.current.isDoubleClick) {
    return;
  }
  variable.current.isDoubleClick = true;
  await AuthCodeCheck(setInputs, authCode, auth, setErrors, setCountDownTime, t,
      setTimer, setAuthTimeLimit, setAuth, emailRef, type);
  variable.current.isDoubleClick = false;
}

export default ModalContent;