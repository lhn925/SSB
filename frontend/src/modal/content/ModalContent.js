import Login from "modal/content/login/Login";
import Join from "modal/content/join/Join";
import Help from "modal/content/help/Help";
import PwUpdateForm from "modal/content/settings/PwUpdateForm";
import {
  ClickBtnAuthCodeCheck, ClickBtnSendCode,
  RegexCheck,
} from "utill/function";
import IdQuery from "modal/content/help/IdQuery";
import {useSelector} from "react-redux";
import Modal from "modal/Modal";
import UserSettingModal from "modal/content/settings/UserSettingModal";

export const JOIN = "JOIN";
export const HELP = "HELP";
export const LOGIN = "LOGIN";

export const PROMPT = "PROMPT";
export const SECURITY_PW_UPDATE = "SECURITY_PW_UPDATE";
export const SECURITY_LOGIN_STATUS = "SECURITY_LOGIN_STATUS";
export const HISTORY_LOGIN_LOG = "HISTORY_LOGIN_LOG";
export const HISTORY_ACTIVITY_LOG = "HISTORY_ACTIVITY_LOG";
export const ID = "ID";
export const PW = "PW";


function ModalContent({closeModal,bc,modalVisible}) {
  const type = useSelector(state => state.modalType.type);
  const helpType = useSelector(state => state.helpType.helpType);
  let width;

  // class 변경
  if (type === SECURITY_LOGIN_STATUS || type === HISTORY_LOGIN_LOG || type === HISTORY_ACTIVITY_LOG) {
    width = "some-class";
  }
  return (
      <>
        {
            modalVisible && <Modal
            width={width}
                visible={modalVisible}
                                   closable={true}
                                   maskClosable={false}
                                   onClose={closeModal}>
              <Content bc={bc} closeModal={closeModal} type={type} helpType={helpType}/>
            </Modal>
        }
      </>
  )
}
function Content({type,helpType,bc,closeModal}) {
  if (type === JOIN) {
    return (
        <>
          <Join RegexCheck={RegexCheck} ClickBtnSendCode={ClickBtnSendCode}
                ClickBtnAuthCodeCheck={ClickBtnAuthCodeCheck}
                closeModal={closeModal} type={type}/>
        </>
    );
  } else if (type === SECURITY_PW_UPDATE) {
    return (
      <>
        <PwUpdateForm closeModal={closeModal}/>
      </>
    )
  } else if (type === HISTORY_LOGIN_LOG || type === HISTORY_ACTIVITY_LOG || type === SECURITY_LOGIN_STATUS) {
    return (
        <>
          <UserSettingModal type={type} closeModal={closeModal}/>
        </>
    )
  }else if (type === HELP && helpType === ID) {
    return (
        <>
          <Help RegexCheck={RegexCheck} ClickBtnSendCode={ClickBtnSendCode}
                ClickBtnAuthCodeCheck={ClickBtnAuthCodeCheck}
                closeModal={closeModal}
                helpType={helpType}/>
        </>
    );
  } else if (type === HELP && helpType === PW) {
    return (
        <>
          <IdQuery RegexCheck={RegexCheck}
                   ClickBtnSendCode={ClickBtnSendCode}
                   ClickBtnAuthCodeCheck={ClickBtnAuthCodeCheck}
                   closeModal={closeModal}
                   helpType={helpType}/>
        </>
    );
  } else if (type === LOGIN)  {
    return (
        <>
          <Login bc={bc} closeModal={closeModal}/>
        </>
    );
  }
}


export default ModalContent;