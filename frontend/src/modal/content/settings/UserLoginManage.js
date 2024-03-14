import {useTranslation} from "react-i18next";
import {LoginDeviceApi} from "utill/api/settings/security/LoginDeviceApi";
import {useEffect, useRef, useState} from "react";
import {BtnOutLine} from "components/button/BtnOutLine";
import {Paging} from "components/paging/Paging";
import {FullDateTime} from "utill/function";
import Modal from "modal/Modal";
import {Input} from "../../../components/input/Input";
import {toast} from "react-toastify";
import {
  LogOutStatusApi
} from "utill/api/settings/security/LogoutStatusApi";

function UserLoginManage({closeModal}) {
  const {t} = useTranslation();

  const [offset, setOffSet] = useState(0);
  const [contents, setContents] = useState([]);
  const [pageable, setPageable] = useState({
    empty: false,
    first: false,
    last: false,
    size: 5, // 페이지 단위
    numberOfElements: 0, // 현재 갖고온 데이터 수
    pageNumber: 0, // 현재 offset
    totalElements: 0,// 전체 데이터 수
    totalPages: 0 // 전체 페이지수
  });

  const variable = useRef({
    isDoubleClick: false // 더블 클릭 방지
  })

  const getLoginDeviceList = (offset) => {
    LoginDeviceApi(offset).then((data) => {
      const values = data.data;
      let copy = [...contents];
      copy = values.content;
      setContents(copy);
      setPageable(
          {
            ...pageable, empty: values.empty,
            first: values.first,
            last: values.last,
            numberOfElements: values.numberOfElements,
            pageNumber: values.pageable.pageNumber,
            totalElements: values.totalElements,
            totalPages: values.totalPages
          });
    }).catch((error) => {
      console.error("error:" + error);
    });
  }
  useEffect(() => {
    getLoginDeviceList(offset);
  }, [offset])

  return (
      <div className="card mainCard">
        <div className="card-body">
          <h4 className="card-title logo-text mainLogo">{t(
              `msg.myPage.sky.login.list`)}</h4>
          <div className="form-group" id="content">
            <table className="table table-striped">
              <tbody>
              <tr className="text-center">
                {getManageHeader(t)}
              </tr>
              <ManageBody getLoginDeviceList={getLoginDeviceList}
                          offSet={offset}
                          variable={variable}
                          pageable={pageable}
                          contents={contents} t={t}/>
              </tbody>
            </table>
          </div>
        </div>
        <Paging t={t} pageable={pageable} offset={offset}
                setOffSet={setOffSet}/>
      </div>
  );
}

function ManageBody({
  getLoginDeviceList,
  offSet,
  pageable,
  contents,
  t,
  variable
}) {
  // 원격 로그아웃시 prompt
  const [promptVisible, setPromptVisible] = useState(false)
  const [session, setSession] = useState(null);
  const [password, setPassword] = useState(null);
  // const [notInStatus, setNotInStatus] = useState([]);
  // const [inStatus, setInStatus] = useState([]);

  const clickOpenPromptEvent = (e) => {
    const value = e.target.dataset.id;
    if (value !== null) {
      setSession(value);
      openPrompt();
    }
  }
  const openPrompt = () => {
    setPromptVisible(true)
  }
  const closePrompt = () => {
    setPromptVisible(false)
  }

  const clickLogoutEvent = async () => {
    alert(t(`msg.device.logout`));
    if (session === "" || session === null) {
      toast.error(t(`errorMsg.server`));
      closePrompt();
      return;
    }
    if (password === "" || password === null) {
      toast.error(t(`msg.password.NotBlank`));
      return;
    }
    if (variable.current.isDoubleClick) {
      return;
    }
    variable.current.isDoubleClick = false;
    let loading = toast.loading(t(`msg.common.logout.progress`));

    const response = await LogOutStatusApi(
        {password: password, session: session});
    toast.dismiss(loading);
    if (response.code === 200) {
      toast.success(t(`msg.device.logoutSuccess`))
      let value = await offSet;
      //
      // empty: false,
      //     first: false,
      //     last: false,
      //     size: 5, // 페이지 단위
      //     numberOfElements: 0, // 현재 갖고온 데이터 수
      //     pageNumber: 0, // 현재 offset
      //     totalElements: 0,// 전체 데이터 수
      //     totalPages: 0 // 전체 페이지수
      //
      // 마지막 페이지에 한개 밖에 없다면
      if (pageable.last && pageable.numberOfElements === 1) {
        value -= 1;
      }
      getLoginDeviceList(value)
      closePrompt();
    } else {
      toast.error(response.data.errorDetails[0].message);
    }
  }

  // // 현재 세션
  const inSessionStatus = contents.filter(value => value.inSession);
  // // 다른 세션
  const notInSessionStatus = contents.filter(value => !value.inSession);
  return (
      <>
        {
          inSessionStatus.map((value, index) => {
            return (
                <tr key={index} className="text-center">
                  <td>{value.os}</td>
                  <td>{value.browser}</td>
                  <td>{value.ip} ({value.countryName})</td>
                  <td>{FullDateTime(value.createdDateTime)}</td>
                  <td>{t(`msg.loginDeviceModal.sky.inSession`)}
                  </td>
                </tr>
            );
          })
        }
        {
          notInSessionStatus.map((value, index) => {
            return (
                <tr key={index} className="text-center">
                  <td>{value.os}</td>
                  <td>{value.browser}</td>
                  <td>{value.ip} ({value.countryName})</td>
                  <td>{FullDateTime(value.createdDateTime)}</td>
                  <td>
                    <BtnOutLine event={clickOpenPromptEvent}
                                data_id={value.session}
                                text={t(`msg.common.sky.logout`)}/>
                  </td>
                </tr>
            );
          })
        }
        {
            promptVisible && <Modal onClose={closePrompt}
                                    visible={promptVisible} closable={true}
                                    maskClosable={false}>
              <form id="form">
                <div className="text-center mb-4">
                  <span className="form-text">{t(
                      `msg.loginDeviceModal.sky.logout.prompt`)}</span>
                </div>
                <div className="form-group">
                  <Input
                      onKeyUp={(e) => setPassword(e.target.value)}
                      type="password"
                      name="password"
                      iconClass="form-pw"
                      placeholder={t(`msg.common.sky.pw`)}/>
                  <div className="d-grid gap-2 col-6 mx-auto">
                    <BtnOutLine
                        event={clickLogoutEvent}
                        text={t(`msg.common.sky.logout`)}
                    />
                  </div>
                </div>
              </form>
            </Modal>
        }
      </>
  );
}

function getManageHeader(t) {
  return <>
    <td><small className="nav-link link_font_color">{t(
        `msg.loginDeviceModal.sky.os`)}</small></td>
    <td><small className="nav-link link_font_color">{t(
        `msg.loginDeviceModal.sky.browser`)}</small></td>
    <td><small className="nav-link link_font_color">{t(
        `msg.loginDeviceModal.sky.login.ip`)}</small></td>
    <td><small className="nav-link link_font_color">{t(
        `msg.loginDeviceModal.sky.first.login`)}</small></td>
    <td><small className="nav-link link_font_color">{t(
        `msg.loginDeviceModal.sky.manage`)}</small></td>
  </>;
}

export default UserLoginManage;