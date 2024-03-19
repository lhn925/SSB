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

export function Settings({location, navigate}) {
  const params = useParams();
  const userInfo = useSelector(state => state.userReducer);
  let root = "settings";
  if (params["root"] !== undefined) {
    root = params.root
  }
  const [modalVisible, setModalVisible] = useState(false)

  const dispatch = useDispatch();
  const {t} = useTranslation();

  const openModal = () => {
    setModalVisible(true)
  }
  const closeModal = () => {
    setModalVisible(false)
  }

  return (
      <div className="container settings_container mt-5">
        <div className="row justify-content-center">
          <div className="col-12 col-md-10">
            <h1 className="text-start settings_title">Settings</h1>
            <div className="tabs">
              <PrivacyNav navigate={navigate} root={root}/>
            </div>

            <div className="col-12 col-md-12" id="settings">
              <ul className="list-group list-group-flush">
                {
                  <>
                    <SettingsContents t={t}
                        dispatch={dispatch}
                        openModal={openModal} root={root} userInfo={userInfo}/>
                    <ModalContent closeModal={closeModal}
                                  modalVisible={modalVisible}/>
                  </>
                }
              </ul>
            </div>
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
  openModal,
}) {
  if (root === "security") {
    return (
        <>
          <SettingsSecurity t={t} dispatch={dispatch} openModal={openModal} userInfo={userInfo}/>
        </>
    );
  } if (root === "history") {
    return (
        <>
          <SettingsHistory t={t} dispatch={dispatch} openModal={openModal}/>
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

function PrivacyNav(props) {
  const prevRootRef = useRef({root: props.root});
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
        {...active, [prevRootRef.current.root]: "", [props.root]: "active"});
    if (prevRoot !== props.root) {
      prevRootRef.current.root = props.root;
    }
  }, [props.root])
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
}