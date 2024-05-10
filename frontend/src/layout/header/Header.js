import {useEffect, useRef, useState} from "react";
import 'css/header.css'
import {Link} from "react-router-dom";
import {useTranslation} from "react-i18next";
import {LOGIN} from "modal/content/ModalContent";
import {useSelector} from "react-redux";

import {
  Dropdown,
} from "react-bootstrap";
import LogoutApi from "utill/api/logout/LogoutApi";
import {persistor} from "store/store";
import {toast} from "react-toastify";
import {USERS_FILE_IMAGE} from "utill/api/ApiEndpoints";
import {removeLocalStorage} from "utill/function";
import {resetAll} from "store/actions";

function Header({
  navigate,
  openModal,
  changeModalType,
  currentAuth,
  userReducer,
  client,
  dispatch,
  bc
}) {
  const {t} = useTranslation();
  const variable = useRef({isDoubleClick: false});
  const [userInfo, setUserInfo] = useState(userReducer);

  const clickBtnLogout = async () => {
    if (variable.current.isDoubleClick) {
      return;
    }
    variable.current.isDoubleClick = true;
    let loading = toast.loading(t(`msg.common.logout.progress`));
    await LogoutApi(
        {Authorization: currentAuth.access, RefreshAuth: currentAuth.refresh})
    toast.dismiss(loading);
    await persistor.purge();
    dispatch(resetAll());
    removeLocalStorage();
    toast.success(t(`msg.common.logout.success`), {
      onClose: () => {
        bc.postMessage({type: "logout"})
      }
    });
  }
  useEffect(() => {
    setUserInfo(userReducer);
  }, [userReducer, currentAuth])
  const openModalHandler = () => {
    changeModalType(LOGIN)
    openModal();
  }
  return (
      <header role="banner"
              className="header">
        <div
            className="justify-content-center header_inner l-container l-fullwidth l-inner-fullwidth">
          <div className="header-left">
            <div>
              <Link to="/"
                    className="text_none_decoration header_font_color">{t(
                  `msg.common.sky.logo`)}</Link>
            </div>
            <div><Link to="/"
                       className="text_none_decoration header_font_color">{t(
                `msg.common.sky.home`)}</Link>
            </div>
            <div><Link to="/feed"
                       className="text_none_decoration header_font_color">{t(
                `msg.common.sky.feed`)}</Link>
            </div>
            <div><Link to="/"
                       className="text_none_decoration header_font_color">{t(
                `msg.common.sky.library`)}</Link>
            </div>
          </div>
          <div className="header-center">
            <form className="header_Search">
              <input
                  className="header_Search_input" type="search" name="q"
                  autoComplete="off"/>
              <button className="header_Search_submit" type="submit"></button>
            </form>
          </div>
          <div className="header-right">
            <div><Link to="/upload"
                       className="text_none_decoration header_font_color">{t(
                `msg.common.sky.upload`)}</Link>
            </div>
            <div>
              {
                userInfo.userId !== null ? <CircularImageDropdown
                    clickBtnLogout={clickBtnLogout}
                    navigate={navigate}
                    client={client}
                    userName={userInfo.userName}
                    pictureUrl={userInfo.pictureUrl}
                /> : <>
                  <button onClick={openModalHandler}
                          className="btn-login-open btn-blue-outline btn-outline">
                    {t(`msg.loginForm.sky.login`)}</button>
                </>
              }
            </div>
            <div>메뉴2</div>
            <div>메뉴2</div>
          </div>
        </div>
      </header>

  )
}

function CircularImageDropdown({
  pictureUrl,
  userName,
  navigate,
  clickBtnLogout,
  client
}) {
  // const sendMessage = () =>{
  //   if (client) {
  //     client.publish({headers:{name:"lim222"},destination:"/app/push",body:JSON.stringify({userId:'lim222',message:'안녕하세요'})});
  //   }
  // }
  return (
      <>
        <Dropdown>
          <Dropdown.Toggle variant="" id="dropdown-basic">
            <img src={USERS_FILE_IMAGE + pictureUrl}/>
          </Dropdown.Toggle>

          <Dropdown.Menu>
            <Dropdown.Item className="profile"
                           onClick={() => navigate(`/${userName}`)}>
              <img/>Profile
            </Dropdown.Item>
            <Dropdown.Item href="#/action-2">
              <img src="css/image/profile2.png" alt=""/>
              Likes
            </Dropdown.Item>
            <Dropdown.Item href="#/action-3">
              <img src="css/image/profile2.png"/>
              Following
            </Dropdown.Item>
            <Dropdown.Item className="profile"
                           onClick={() => navigate(`/settings`)}>
              settings
            </Dropdown.Item>
            <Dropdown.Item className="profile" onClick={clickBtnLogout}>
              <img/>logout
            </Dropdown.Item>
          </Dropdown.Menu>
        </Dropdown>
      </>
  );
}

export default Header;