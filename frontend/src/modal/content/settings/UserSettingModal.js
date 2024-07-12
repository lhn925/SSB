import {useTranslation} from "react-i18next";
import {LoginDeviceApi} from "utill/api/settings/security/LoginDeviceApi";
import {useEffect, useRef, useState} from "react";
import {BtnOutLine} from "components/button/BtnOutLine";
import {Paging} from "components/paging/Paging";
import {DateFormat, FullDateTime, DateToDay} from "utill/function";
import Modal from "modal/Modal";
import {Input} from "components/input/Input";
import {toast} from "react-toastify";
import {
  LogOutStatusApi
} from "utill/api/settings/security/LogoutStatusApi";
import {
  HISTORY_ACTIVITY_LOG,
  HISTORY_LOGIN_LOG,
  SECURITY_LOGIN_STATUS
} from "modal/content/ModalContent";
import {UserLogApi} from "utill/api/settings/history/UserLogApi";

function UserSettingModal({type}) {
  const {t} = useTranslation();
  const [dateObject, setDateObject] = useState({
    startDate: null,
    endDate: null,
    startDateValue: null,
    endDateValue: null,
    maxDate: null,
    minDate: null
  });

  const [code, setCode] = useState(``);
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

  const startDateRef = useRef(null);
  const endDateRef = useRef(null);
  const variable = useRef({
    isDoubleClick: false // 더블 클릭 방지
  })

  function dateSettings() {
    if (type !== SECURITY_LOGIN_STATUS && dateObject.startDate
        && dateObject.endDate) {
      return dateObject;
    }

    const today = new Date();
    const start = new Date(today.setDate(today.getDate() - 7)); // 시작 날짜를 오늘로부터 7일 전으로 설정
    const end = new Date(); // 종료 날짜는 오늘 날짜

    // 최소 제한 날짜 설정 로직
    const minMonthRange = type === HISTORY_LOGIN_LOG ? 3 : 6; // 로그인 로그인 경우 3개월, 그 외 6개월
    const min = new Date(new Date().setMonth(today.getMonth() - minMonthRange));

    // 날짜 포맷팅
    const startVal = DateFormat(start);
    const endVal = DateFormat(end);
    const minVal = `${min.getFullYear()}-${min.getMonth() + 1 < 10 ? "0"
        + (min.getMonth() + 1) : min.getMonth() + 1}-${min.getDate()}`;

    // 새로운 상태 객체 생성
    const newState = {
      startDate: start,
      endDate: end,
      startDateValue: startVal,
      endDateValue: endVal,
      maxDate: endVal,
      minDate: minVal
    };

    setInputAttributes(endDateRef, {value: endVal, min: minVal, max: endVal});
    setInputAttributes(startDateRef,
        {value: startVal, min: minVal, max: endVal})

    // 상태 업데이트 로직은 함수 밖에서 수행
    return newState;

    // return copyObject;
  }

  function onDateChange(e) {
    const {valueAsDate, name} = e.target;
    let dateValueKey = "startDateValue";
    if (name === "endDate") {
      dateValueKey = "endDateValue";
    }

    setDateObject((date) => {
      return {
        ...date,
        [name]: valueAsDate,
        [dateValueKey]: DateFormat(valueAsDate)
      }
    })
  }

  function setInputAttributes(ref, {value, min, max}) {
    if (ref.current) {
      ref.current.value = value;
      ref.current.min = min;
      ref.current.max = max;
    }
  }

  const clickLogDateSearchBtn = async () => {
    if (variable.current.isDoubleClick) {
      return;
    }
    variable.current.isDoubleClick = true;
    const startDateValue = dateObject.startDateValue;
    const endDateValue = dateObject.endDateValue;
    const startTime = new Date(startDateValue).getTime();
    const endTime = new Date(endDateValue).getTime();


    if (startTime > endTime) {
      toast.error(t(`errorMsg.error.dateError`));
      variable.current.isDoubleClick = false;
      return;
    }

    variable.current.isDoubleClick = false;
    const contentsList = await getContentsList(offset, type, startDateValue,
        endDateValue);

    updatePageable(setPageable, contentsList.pageable);
    updateContents(setContents, contentsList.contents);
  };

  useEffect(() => {
  }, [contents, pageable])

  useEffect(() => {
    async function valueSetting() {
      let dateObject = dateSettings();
      setDateObject(dateObject);
      if (code === ``) {
        switch (type) {
          case SECURITY_LOGIN_STATUS:
            setCode(`msg.myPage.sky.login.list`);
            break;
          case HISTORY_LOGIN_LOG:
            setCode(`msg.myPage.sky.login.log`);
            break;
          default:
            setCode(`msg.myPage.sky.activityLog`);
            break;
        }
      }

      const contentsList = await getContentsList(offset, type,
          dateObject.startDateValue, dateObject.endDateValue);

      if (contentsList?.contents == null) {
        return;
      }

      updatePageable(setPageable, contentsList.pageable);
      updateContents(setContents, contentsList.contents);
    }

    valueSetting().then(() => {
    });
  }, [offset])

  return (
      <div className="card mainCard">
        <div className="card-body">
          <h4 className="card-title logo-text mainLogo">
            {t(code)}
          </h4>

          {
              type !== SECURITY_LOGIN_STATUS && <>
                <div className="text-center mb-4">
                  {
                    type === HISTORY_ACTIVITY_LOG ?
                        <span className="form-text">{t(
                            `msg.userActivityLogInfo`)}</span>
                        : <span className="form-text">{t(
                            `msg.userLoginLogInfo`)}</span>
                  }

                </div>
                <div id="rangeDate" className="input-group mb-3 mt-3">
                  <br/>

                  <input type="date"
                         name="startDate"
                         onChange={onDateChange}
                         ref={startDateRef}
                         id="startDate"
                         className="form-control"/>
                  <h3>~</h3>
                  <input type="date"
                         name="endDate"
                         ref={endDateRef}
                         onChange={onDateChange}
                         className="form-control"/>
                  <button className="btn btn-primary"
                          onClick={clickLogDateSearchBtn}
                          id="searchDateBtn">조회
                  </button>
                </div>
              </>
          }

          <div className="form-group" id="content">
            <table className="table table-striped">
              <tbody>
              {
                <SettingsModalContents setContents={setContents}
                                       setPageable={setPageable} type={type}
                                       offset={offset} pageable={pageable}
                                       contents={contents} t={t}
                                       variable={variable}/>
              }

              </tbody>
            </table>
          </div>
        </div>
        <Paging t={t} pageable={pageable} offset={offset}
                setOffSet={setOffSet}/>
      </div>
  );
}

async function getContentsList(offset, type, startDateValue, endDateValue) {
  let response = null;
  if (type === SECURITY_LOGIN_STATUS) {
    response = await LoginDeviceApi(offset);
  } else {
    response = await UserLogApi(type, offset, startDateValue,
        endDateValue);
  }
  if (response.code === 200) {
    const values = response.data;
    let copyPageable = {
        empty: values.empty,
        first: values.first,
        last: values.last,
        numberOfElements: values.numberOfElements,
        pageNumber: values.pageable.pageNumber,
        totalElements: values.totalElements,
        totalPages: values.totalPages
      }
    return {contents: values.content, pageable: copyPageable};
  } else if (response.code === 500) {
    toast.error(response.data);
  }
}

function SettingsModalContents(
    {setContents, setPageable, type, offset, pageable, contents, t, variable}) {

  if (type === SECURITY_LOGIN_STATUS) {
     return (
         <ManageBody t={t} variable={variable} contents={contents} type={type}
             pageable={pageable} setPageable={setPageable}
             offSet={offset} setContents={setContents}/>);
  } else {
    return (
        <LogBody t={t} contents={contents} type={type}/>
    )
  }
}

function LogBody({
  type,
  contents,
  t
}) {
  const isType = type === HISTORY_LOGIN_LOG;
    return (
        <>
          <tr className="text-center">
            {getDynamicHeader(type, t)}
          </tr>
          {
            contents.length !== 0 ?
                contents.map((value, index) => {
                  return (
                      <tr key={index} className="text-center">
                        <td>{FullDateTime(value.createdDateTime)}</td>
                        <td>{isType ? value.ip : value.chaContent}</td>
                        <td>{isType ? value.countryCode : value.chaMethod}</td>
                        <td>{isType ? value.userAgent : value.ip}</td>
                      </tr>
                  );
                }) :<tr key={1} className="text-center"><td colSpan={5}>{t(`msg.myPage.sky.log.notBlank`)}</td></tr>
          }
        </>
    );
}

function ManageBody({
  offSet,
  pageable,
  contents,
  setPageable, setContents,
  t,
  variable, type
}) {

  // 원격 로그아웃시 prompt
  const [promptVisible, setPromptVisible] = useState(false)
  const [session, setSession] = useState(null);
  const [password, setPassword] = useState(null);

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
    variable.current.isDoubleClick = true;
    let loading = toast.loading(t(`msg.common.logout.progress`));

    const response = await LogOutStatusApi(
        {password: password, session: session});
    toast.dismiss(loading);
    variable.current.isDoubleClick = false;
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
      const data = await getContentsList(value, type);
      updatePageable(setPageable, data.pageable);
      updateContents(setContents, data.contents);

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
        <tr className="text-center">
          {getDynamicHeader(type,t)}
        </tr>
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



const updatePageable = (setPageable, newData) => {
    setPageable((prev) =>{ return {
      ...prev,...newData
    }})
}

const updateContents = (setContents, newData) => {
  setContents(prevContents => {
    return newData
  });
}

function getDynamicHeader(type, t) {
  let titleList;

  switch (type) {
    case HISTORY_ACTIVITY_LOG:
      titleList = [
        `msg.sky.createdDate`,
        `msg.sky.chaContent`,
        `msg.sky.chaMethod`,
        `msg.sky.chaIp`
      ];
      break;
    case SECURITY_LOGIN_STATUS:
      titleList = [
        `msg.loginDeviceModal.sky.os`,
        `msg.loginDeviceModal.sky.browser`,
        `msg.loginDeviceModal.sky.login.ip`,
        `msg.loginDeviceModal.sky.first.login`,
        `msg.loginDeviceModal.sky.manage`
      ];
      break;
    default:
      titleList = [
        `msg.sky.createdDate`,
        `msg.sky.login.ip`,
        `msg.sky.countryName`,
        `msg.sky.device`
      ];
      break;
  }

  return (
      <>
        {titleList.map((value, index) => (
            <td key={index}>
              <small className="nav-link link_font_color">{t(value)}</small>
            </td>
        ))}
      </>
  );
}

export default UserSettingModal;