import "css/profile/profile.css"
import "css/common/tracks.css"
import "css/profile/trackShow.css"
import "css/profile/app.css"
import {USERS_FILE_IMAGE} from "utill/api/ApiEndpoints";
import {
  URL_SETTINGS,
  URL_SETTINGS_HISTORY,
  URL_SETTINGS_SECURITY
} from "content/UrlEndpoints";
import Nav from "components/nav/Nav";
import {Btn} from "components/button/Btn";
import {BtnOutLine} from "components/button/BtnOutLine";
import {useTranslation} from "react-i18next";
import {useEffect, useRef, useState} from "react";
import {useDropdown} from "context/dropDown/DropdownProvider";
import {acceptArray} from "utill/enum/Accept";
import {encodeFileToBase64, ssbConfirm} from "utill/function";
import PictureUpdateApi from "utill/api/picture/PictureUpdateApi";
import {toast} from "react-toastify";
import profile2 from "css/image/profile2.png";
import PictureDeleteApi from "utill/api/picture/PictureDeleteApi";
import useModal from "hoks/modal/useModal";
import {PROFILE_EDIT} from "../../modal/content/ModalContent";

export function Profile({header, setHeader, myUserInfo, cachedUser,useModal1}) {

  const {t} = useTranslation();
  const {isDropdownOpen, handleClick} = useDropdown();
  const pictureFileRef = useRef();
  const isMyProfile = myUserInfo.userReducer.id === header.id;
  const tabs = [
    {id: "all", title: "All", url: URL_SETTINGS},
    {id: "popular", title: "Popular tracks", url: URL_SETTINGS_SECURITY},
    {id: "tracks", title: "Tracks", url: URL_SETTINGS_HISTORY},
    {id: "Albums", title: "Albums", url: URL_SETTINGS_HISTORY},
    {id: "Playlists", title: "Playlists", url: URL_SETTINGS_HISTORY},
    {id: "Reposts", title: "Reposts", url: URL_SETTINGS_HISTORY},
  ];
  useEffect(() => {

    if (header.id === -1) {
      return;
    }

    // 닉네임 변경시 즉시 반영
    const isUserNameChange = header.userName !== myUserInfo.userReducer.userName;
    if (isMyProfile && isUserNameChange) {
       const newHeader = {...header,userName:myUserInfo.userReducer.userName}
      cachedUser.addUsers(newHeader);
      setHeader(newHeader);
      return;
    }

    cachedUser.addUsers(header);
  }, [header,myUserInfo.userReducer.userName])
  // 파일 btn event
  const clickImgUploadBtnEvent = () => {
    pictureFileRef.current.click();
  }

  const clickEditBtnEvent = () => {
    useModal1.changeModalType(PROFILE_EDIT);
    useModal1.openModal();
  }
  const changePictureUploadEvent = () => {
    const {files} = pictureFileRef.current;
    if (files.length === 0) {
      return;
    }
    ssbConfirm("", "t) 프로필 사진을 정말 변경하시겠습니까?", true, "t) 변경", "t) 취소",
        async () => {
          const formData = new FormData();
          formData.append("file", files[0]);
          const response = await PictureUpdateApi(formData);
          if (response.code === 200) {
            const pictureUrl = response.data.storeFileName;
            myUserInfo.updatePictureUrl(pictureUrl);
            setHeader({...header, pictureUrl: pictureUrl})
            toast.success(t(`프로필 사진이 변경 되었습니다.`));
          } else {
            toast.error(t(response.data?.errorDetails[0].message))
          }
          pictureFileRef.current.value = null;
        }, () => {
          pictureFileRef.current.value = null;
        })
  }
  const pictureDeleteClickEvent = () => {
    if (!header.pictureUrl) {
      return;
    }
    ssbConfirm("t) Are you sure?", "t)  사진을 정말 삭제 하시겠습니까?", true, "t) 삭제", "t) 취소",
        async () => {
          const response = await PictureDeleteApi();
          if (response.code === 200) {
            myUserInfo.updatePictureUrl(null);
            setHeader({...header, pictureUrl: null})
            toast.success(t(`t) 프로필 사진이 삭제 되었습니다.`));
          } else {
            toast.error(t(response.data?.errorDetails[0].message))
          }
        }, () => {
        })
  }
  return (
      <div
          className='track-show-page container justify-content-center l-container'>
        {/*헤더*/}
        <div className="profile-container col-12">
          {
              isMyProfile && isDropdownOpen && <div
                  className="dropdownMenu g-z-index-overlay">
                <ul className="list-group">
                  <li className="list-group-item">
                    <button className="btn btn-upload" onClick={() => {
                      clickImgUploadBtnEvent();
                    }}>
                      Replace Image
                    </button>
                  </li>
                  {
                      header.pictureUrl && <li className="list-group-item">
                        <button className="btn btn-upload" onClick={() => {
                          pictureDeleteClickEvent();
                        }}>
                          Delete Image
                        </button>
                      </li>
                  }
                </ul>
              </div>
          }
          <div className='user-show-container'>
            <div className='user-show-image-container'>
              <div style={{
                backgroundImage: `url(${header.pictureUrl ? USERS_FILE_IMAGE
                    + header.pictureUrl : profile2})`,
                width: `240px`,
                height: `200px`,
                backgroundSize: `cover`,
                backgroundPosition: `center`
              }}></div>
              {isMyProfile && <div className={(isDropdownOpen && "d-block")
                  + " upload-btn-wrapper"}>
                <button className="btn btn-upload btn-upload-picture  "
                        onClick={(e) => {
                          handleClick(e);
                        }}>
                  {t(`msg.common.image.btn`)}
                </button>
                <input type="file" name="pictureImg" ref={pictureFileRef}
                       onChange={changePictureUploadEvent} multiple={false}
                       accept={acceptArray.toString()}
                       className="visually-hidden"/>
              </div>}
              {/*{editUser}*/}
            </div>
            <div className='user-show-detail'>
              <div className='user-sd-top'>
                <div className='user-sd-info'>
                  <div className='user-sd-title'>{header.userName}</div>
                  {/*<div className='user-sd-other'>{header.email}</div>*/}
                  {/*<div className='user-sd-other'>{user.location || "vibesphere, Earth"}</div>*/}
                </div>
              </div>
            </div>
          </div>


          {/*메인 상단 nav*/}
          <div className='track-show-container-bottom'>
          <span className='track-index-page-container'>
            <div className="userInfoBar nav-tabs">
              <div className="content-tabs tabs">
                {/*<PrivacyNav navigate={navigate} root={root}/>*/}
                <Nav currentRoot={'test'} tabs={tabs}/>
              </div>
              <div className="userInfoBar-buttons">
               {
                 !isMyProfile ? <> <Btn text={"Station"}/>
                   <BtnOutLine text={"Following"}/>
                   <Btn text={"share"}/>
                   <BtnOutLine text={"안녕"}/>
                   <Btn text={"안녕"}/></> : <BtnOutLine text="Edit" event={clickEditBtnEvent }/>

               }
              </div>
            </div>


            {/*메인*/}
            <div className='track-index-container'>
            </div>


            {/*사이드바*/}
            <div className="sidebar-placeholder">
              <div className="user-stats">
                <div className="us-track-num">
                  <p>t) Followers</p>
                  {/*<p>{parseInt(tracks.length)}</p>*/}
                  <p>{header.followerCount}</p>
                </div>
                <div className="vertical-line"></div>
                  <div className="us-track-num">
                  <p>t) following</p>
                    {/*<p>{parseInt(tracks.length)}</p>*/}
                    <p>{header.followingCount}</p>
                </div>
                <div className="vertical-line"></div>
                  <div className="us-track-num">
                  <p>t) Tracks</p>
                    <p>{header.trackTotalCount}</p>
                </div>

              </div>
              <div className="no-about">{}</div>
              <div className="ad-container">
                <a href="https://github.com/Mpompili" target="_blank"><img
                    src="https://res.cloudinary.com/mpompili/image/upload/v1526013412/gotogithub.jpg"
                    alt=""/></a>
              </div>
              <div className="ad-container">
                <a href="https://www.linkedin.com/in/michael-pompili-916a0837/"
                   target="_blank"><img
                    src="https://res.cloudinary.com/mpompili/image/upload/v1526335358/linkedinad.jpg"
                    alt=""/></a>
              </div>
              <div className="extraspace"></div>
            </div>
          </span>
          </div>
        </div>
      </div>
  )
}