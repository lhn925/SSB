import "css/settings/settings.css"
import {Link, useParams} from "react-router-dom";
import {Nav} from "react-bootstrap";
import {useEffect, useRef, useState} from "react";

export const Security = (props) => {
  const params = useParams();

  let root = "settings";
  if (params["root"] !== undefined) {
    root = params.root
  }

  return (
      <div className="container settings_container mt-5">
        <div className="row justify-content-center">
          <div className="col-12 col-md-10">
            <h1 className="text-start settings_title">Settings</h1>
            <div className="tabs">
              <PrivacyNav navigate={props.navigate} root={root}/>
            </div>
            <div className="col-12 col-md-12" id="account">
              <ul className="list-group list-group-flush">
                <li className="settings_li_header d-flex">
                  <h3 className="settings_h3_title ms-2">가입하신 이메일 주소</h3>
                </li>
                <li className="list-group-item emailImg d-flex align-items-center flex-wrap">
                  <h6 className="mb-0">
                    <svg xmlns="http://www.w3.org/2000/svg"
                         className="feather feather-globe mr-2 icon-inline">
                    </svg>
                  </h6>
                  <span className="ms-4" >2221325@naver.com</span>
                </li>
                <li className="settings_li_header d-flex">
                  <h3 className="settings_h3_title ms-2">로그인 아이디</h3>
                </li>
                <li className="list-group-item profileImg d-flex align-items-center flex-wrap">
                  <h6 className="mb-0">
                    <svg xmlns="http://www.w3.org/2000/svg"
                         className="feather feather-globe mr-2 icon-inline">
                    </svg>
                  </h6>
                  <span className="ms-4" >lim222</span>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
  )
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
    setActive({...active, [prevRootRef.current.root]: "", [props.root]: "active"});
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
                  to="/settings">Account</Link>
          </li>
          <li className="nav-item">
            <Link value="content"
                  className={"nav-link link_font_color " + active.security}
                  to="/settings/security">Security</Link>
          </li>

          <li className="nav-item">
            <Link value="content"
                  className={"nav-link link_font_color " + active.history}
                  to="/settings/history">Manage History</Link>
          </li>

          <li className="nav-item">
            <Link value="content"
                  className={"nav-link link_font_color " + active.notifications}
                  to="/settings/notifications">Notifications</Link>
          </li>
        </ul>
      </>
  )
}