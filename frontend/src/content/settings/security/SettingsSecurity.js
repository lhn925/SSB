import {BtnOutLine} from "components/button/BtnOutLine";
import {modalActions} from "store/modalType/modalType";
import {
  SECURITY_LOGIN_STATUS,
  SECURITY_PW_UPDATE
} from "modal/content/ModalContent";

export function SettingsSecurity({t,userInfo,openModal,dispatch}) {

  const modal = (type) => {
    dispatch(modalActions.changeType({type:type}))
    openModal();
  }
  return (
      <>
        <li className="settings_li_header d-flex">
          <h3 className="settings_h3_title ms-2">{t(`msg.common.sky.pw`)}</h3>
        </li>
        <li className="list-group-item d-flex align-items-center flex-wrap">
          <h6 className="mb-0">
            <svg xmlns="http://www.w3.org/2000/svg"
                 className="feather feather-globe mr-2 icon-inline">
            </svg>
          </h6>
          <BtnOutLine event={()=> modal(SECURITY_PW_UPDATE)
          } text={t(`msg.help.sky.pw.change`)} id="pwUpdateModal"/>
        </li>
        <li className="settings_li_header d-flex">
          <h3 className="settings_h3_title ms-2">{t(`msg.myPage.sky.login.list`)}</h3>
        </li>
        <li className="list-group-item d-flex align-items-center flex-wrap">
          <h6 className="mb-0">
            <svg xmlns="http://www.w3.org/2000/svg"
                 className="feather feather-globe mr-2 icon-inline">
            </svg>
          </h6>
          <BtnOutLine event={()=> modal(SECURITY_LOGIN_STATUS)
          } text="로그인 기기 관리" id="loginStatusModal"/>
        </li>
        <li className="settings_li_header d-flex">
          <h3 className="settings_h3_title ms-2">{t(`msg.myPage.sky.overseas.login.block`)}</h3>
        </li>
        <li className="list-group-item d-flex align-items-center flex-wrap">
          <div className="form-check form-switch">
            <input className="form-check-input" type="checkbox"
                   role="switch"/>
          </div>
        </li>
      </>
  )

}