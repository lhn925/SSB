import "css/settings/settings.css"
import {Link, useParams} from "react-router-dom";
import {useEffect, useRef, useState} from "react";
import {useDispatch, useSelector} from "react-redux";
import {SettingsSecurity} from "content/settings/security/SettingsSecurity";
import {SettingsAccount} from "content/settings/account/SettingsAccount";
import ModalContent from "modal/content/ModalContent";
import {useTranslation} from "react-i18next";
import {
  URL_SETTINGS,
  URL_SETTINGS_HISTORY, URL_SETTINGS_NOTIFICATIONS,
  URL_SETTINGS_SECURITY
} from "content/UrlEndpoints";
import {SettingsHistory} from "./history/SettingsHistory";
import Nav from "components/nav/Nav";
import {modalActions} from "store/modalType/modalType";

export function Settings({modal,dispatch,openModal,changeModalType}) {
  const params = useParams();

  const tabs = [
    {id: "settings", title: "Account", url: URL_SETTINGS},
    {id: "security", title: "Security", url: URL_SETTINGS_SECURITY},
    {id: "history", title: "Manage History", url: URL_SETTINGS_HISTORY},
    {id: "notifications", title: "Notifications", url: URL_SETTINGS_NOTIFICATIONS},
  ];

  const userInfo = useSelector(state => state.userReducer);

  const root = params.root === undefined ? "settings" : params.root;

  const {t} = useTranslation();

  return (
        <div className="row justify-content-center settings_container ">
          <div className="col-12 col-md-10 mt-5">
            <h1 className="text-start settings_title">Settings</h1>
            <div className="tabs">
              {/*<PrivacyNav navigate={navigate} root={root}/>*/}
              <Nav currentRoot={root} tabs={tabs}/>
            </div>

            <div className="col-12 col-md-12" id="settings">
              <ul className="list-group list-group-flush">
                {
                  <>
                    <SettingsContents t={t}
                                      dispatch={dispatch}
                                      changeModalType={changeModalType}
                                      openModal={openModal} root={root}
                                      userInfo={userInfo}/>
                  </>
                }
              </ul>
            </div>
          </div>
        </div>
  )
}

function SettingsContents({
  t,
  root,
  userInfo,
  dispatch,
  changeModalType,
  openModal,
}) {
  if (root === "security") {
    return (
        <>
          <SettingsSecurity t={t}
                            dispatch={dispatch}
                            changeModalType={changeModalType} openModal={openModal}
                            userInfo={userInfo}/>
        </>
    );
  }
  if (root === "history") {
    return (
        <>
          <SettingsHistory t={t}
                           changeModalType={changeModalType} openModal={openModal}/>
        </>
    );
  } else {
    return (
        <>
          <SettingsAccount userInfo={userInfo}/>
        </>
    );
  }
}

/*
function PrivacyNav({root}) {
  const prevRootRef = useRef({root: root});
  const [active, setActive] = useState({
    settings: "",
    security: "",
    history: "",
    notifications: ""
  });
  useEffect(() => {
    let prevRoot = prevRootRef.current.root;
    console.log("prevRoot : " + prevRoot)
    setActive(
        {...active, [prevRootRef.current.root]: "", [root]: "active"});
    if (prevRoot !== root) {
      prevRootRef.current.root = root;
    }
  }, [root])
  return (
      <>
        <ul className="nav nav-tabs">
          <li className="nav-item">
            <Link value="settings"
                  className={"nav-link link_font_color " + active.settings}
                  to={URL_SETTINGS}>Account</Link>
          </li>
          <li className="nav-item">
            <Link value="content"
                  className={"nav-link link_font_color " + active.security}
                  to={URL_SETTINGS_SECURITY}>Security</Link>
          </li>

          <li className="nav-item">
            <Link value="content"
                  className={"nav-link link_font_color " + active.history}
                  to={URL_SETTINGS_HISTORY}>Manage History</Link>
          </li>

          <li className="nav-item">
            <Link value="content"
                  className={"nav-link link_font_color " + active.notifications}
                  to={URL_SETTINGS_NOTIFICATIONS}>Notifications</Link>
          </li>
        </ul>
      </>
  )
}*/
