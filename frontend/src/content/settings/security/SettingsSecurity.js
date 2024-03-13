import {BtnOutLine} from "components/ button/BtnOutLine";
import {modalActions} from "store/modalType/modalType";
import {SECURITY_PW_UPDATE} from "modal/content/ModalContent";

export function SettingsSecurity({userInfo,openModal,dispatch}) {

  const modal = (type) => {
    dispatch(modalActions.changeType({type:type}))
    openModal();
  }
  return (
      <>
        <li className="settings_li_header d-flex">
          <h3 className="settings_h3_title ms-2">비밀번호</h3>
        </li>
        <li className="list-group-item d-flex align-items-center flex-wrap">
          <h6 className="mb-0">
            <svg xmlns="http://www.w3.org/2000/svg"
                 className="feather feather-globe mr-2 icon-inline">
            </svg>
          </h6>
          <BtnOutLine event={()=> modal(SECURITY_PW_UPDATE)
          } text="비밀번호 변경" id="pwUpdateModal"/>
        </li>
        <li className="settings_li_header d-flex">
          <h3 className="settings_h3_title ms-2">로그인 관리</h3>
        </li>
        <li className="list-group-item profileImg d-flex align-items-center flex-wrap">
          <h6 className="mb-0">
            <svg xmlns="http://www.w3.org/2000/svg"
                 className="feather feather-globe mr-2 icon-inline">
            </svg>
          </h6>
          <span className="ms-4">{userInfo.userId}</span>
        </li>
        <li className="settings_li_header d-flex">
          <h3 className="settings_h3_title ms-2">해외 로그인 차단</h3>
        </li>
        <li className="list-group-item earthImg d-flex align-items-center flex-wrap">
          <h6 className="mb-0">
            <svg xmlns="http://www.w3.org/2000/svg"
                 className="feather feather-globe mr-2 icon-inline">
            </svg>
          </h6>
          <span className="ms-4">{userInfo.userId}</span>
        </li>
      </>
  )

}