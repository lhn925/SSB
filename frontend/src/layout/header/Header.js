import {useEffect, useState} from "react";
import 'css/header.css'
import {Link} from "react-router-dom";
import Modal from "modal/Modal";
import Login from "modal/content/login/Login"
import {useTranslation} from "react-i18next";
import Content from "modal/content/Content";
import {useDispatch, useSelector} from "react-redux";
import {modalActions} from "store/modalType/modalType";

function Header() {
  const [modalVisible, setModalVisible] = useState(false)

  const dispatch = useDispatch();
  const {t} = useTranslation();
  const [content, setContent] = useState();
  const type = useSelector(state => state.modalType.type);
  const helpType = useSelector(state => state.helpType.helpType);
  const openModal = () => {
    dispatch(modalActions.changeType({type:"LOGIN"}));
    setModalVisible(true)
  }
  const closeModal = () => {
    setModalVisible(false)
  }

  useEffect(() => { // modal 내용 변경
    setContent(Content({type,closeModal,helpType}))
  },[type,helpType]);

  return (
      <header role="banner"
              className="header">
        <div className="justify-content-center header_inner l-container l-fullwidth l-inner-fullwidth">
          <div className="header-left">
            <div><Link to="/"
                       className="text_none_decoration header_font_color">{t(`msg.common.sky.logo`)}</Link>
            </div>
            <div><Link to="/"
                       className="text_none_decoration header_font_color">{t(`msg.common.sky.home`)}</Link>
            </div>
            <div><Link to="/feed"
                       className="text_none_decoration header_font_color">{t(`msg.common.sky.feed`)}</Link>
            </div>
            <div><Link to="/"
                       className="text_none_decoration header_font_color">{t(`msg.common.sky.library`)}</Link>
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
              <button className="btn-login-open btn-blue-outline btn-outline" onClick={openModal}>{t(`msg.loginForm.sky.login`)}</button>
              {
                  modalVisible && <Modal visible={modalVisible} closable={true} maskClosable={false} onClose={closeModal}>
                    {content}
                  </Modal>
              }
            </div>
            <div>메뉴2</div>
            <div>메뉴2</div>
          </div>
        </div>
      </header>)
}

export default Header;