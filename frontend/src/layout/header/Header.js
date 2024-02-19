import {useEffect, useRef, useState} from "react";
import 'css/header.css'
import {Link} from "react-router-dom";
import Modal from "modal/Modal";
import {useTranslation} from "react-i18next";
import ModalContent from "modal/content/ModalContent";
import {useDispatch, useSelector} from "react-redux";
import {modalActions} from "store/modalType/modalType";
import {
  Dropdown,
} from "react-bootstrap";
import {useNavigate} from "react-router";
import LogoutApi from "utill/api/logout/LogoutApi";
import {persistor} from "store/store";
import {toast} from "react-toastify";

function Header(props) {
  const auth = useSelector(state => state.authReducer);
  const user = useSelector(state => state.userReducer);

  const [modalVisible, setModalVisible] = useState(false)
  const dispatch = useDispatch();
  const {t} = useTranslation();

  const navigate = useNavigate();
  const variable = useRef({isDoubleClick: false});
  const clickBtnLogout = async () => {
    if (variable.current.isDoubleClick) {
      return;
    }
    variable.current.isDoubleClick = true;
    let loading = toast.loading(t(`msg.common.logout.progress`));
    LogoutApi({Authorization: auth.access, RefreshAuth: auth.refresh})
    .then(() => {
      persistor.purge();
      props.bc.postMessage({type: "logout"});
      toast.dismiss(loading);
      toast.success(t(`msg.common.logout.success`));
    }).catch(() => {
      toast.error(t("errorMsg.server"));
    })
  }

  useEffect(() => {
  },[user])
  const openModal = () => {
    dispatch(modalActions.changeType({type: "LOGIN"}));
    setModalVisible(true)
  }
  const closeModal = () => {
    setModalVisible(false)
  }
  return (
      <header role="banner"
              className="header">
        <div
            className="justify-content-center header_inner l-container l-fullwidth l-inner-fullwidth">
          <div className="header-left">
            <div>
              <Link to="/"
                       className="text_none_decoration header_font_color">{t(`msg.common.sky.logo`)}</Link>
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
            <div>{t(`msg.common.sky.upload`)}</div>
            <div>
              {
                user.userId !== null ? <CircularImageDropdown
                    clickBtnLogout={clickBtnLogout}
                    navigate={navigate}
                    userId={user.userId}/> : <>
                  <button onClick={openModal} className="btn-login-open btn-blue-outline btn-outline" >
                    {t(`msg.loginForm.sky.login`)}</button>
                  <ModalContent bc={props.bc} modalVisible={modalVisible} closeModal={closeModal}/>

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

function CircularImageDropdown({userId, navigate, clickBtnLogout}) {

  return (
      <>
        <Dropdown>
          <Dropdown.Toggle variant="" id="dropdown-basic">
            <img src={"./users/file/picture/" + userId}/>
          </Dropdown.Toggle>

          <Dropdown.Menu>
            <Dropdown.Item className="profile"
                           onClick={() => navigate(`/${userId}`)}>
              <img/>Profile
            </Dropdown.Item>
            <Dropdown.Item href="#/action-2">
              <img src="./../../css/image/profile2.png"/>
              Likes
            </Dropdown.Item>
            <Dropdown.Item href="#/action-3">
              <img src="./../../css/image/profile2.png"/>
              Following
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