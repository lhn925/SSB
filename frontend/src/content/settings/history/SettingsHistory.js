import {BtnOutLine} from "components/button/BtnOutLine";
import {modalActions} from "store/modalType/modalType";
import {
  HISTORY_ACTIVITY_LOG,
  HISTORY_LOGIN_LOG
} from "modal/content/ModalContent";
import {useRef, useState} from "react";


export function SettingsHistory({t, openModal, dispatch}) {
  const variable = useRef({
    isDoubleClick: false // 더블 클릭 방지
  })
  const modal = (type) => {
    dispatch(modalActions.changeType({type: type}))
    openModal();
  }
  return (
      <>
        <li className="settings_li_header d-flex">
          <h3 className="settings_h3_title ms-2">{t(`msg.myPage.sky.login.log`)}</h3>
        </li>
        <li className="list-group-item d-flex align-items-center flex-wrap">
          <h6 className="mb-0">
            <svg xmlns="http://www.w3.org/2000/svg"
                 className="feather feather-globe mr-2 icon-inline">
            </svg>
          </h6>
          <BtnOutLine event={() => modal(HISTORY_LOGIN_LOG)
          } text={t(`msg.myPage.sky.loginLog.list`)} id="pwUpdateModal"/>
        </li>
        <li className="settings_li_header d-flex">
          <h3 className="settings_h3_title ms-2">{t(
              `msg.myPage.sky.activityLog`)}</h3>
        </li>
        <li className="list-group-item d-flex align-items-center flex-wrap">
          <h6 className="mb-0">
            <svg xmlns="http://www.w3.org/2000/svg"
                 className="feather feather-globe mr-2 icon-inline">
            </svg>
          </h6>
          <BtnOutLine event={() => modal(HISTORY_ACTIVITY_LOG)
          } text={t(`msg.myPage.sky.activity.list`)} id="loginStatusModal"/>
        </li>

      </>
  )

}