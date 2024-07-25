import "css/profile/profile.css"
import "css/common/tracks.css"
import "css/profile/trackShow.css"
import "css/profile/app.css"
import "css/profile/sound-badge.css"
import "css/profile/sound-badge-list.css"
import "css/profile/image.css"
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
import React, {useEffect, useRef, useState} from "react";
import {useDropdown} from "context/dropDown/DropdownProvider";
import {acceptArray} from "utill/enum/Accept";
import {durationTime, encodeFileToBase64, ssbConfirm} from "utill/function";
import PictureUpdateApi from "utill/api/picture/PictureUpdateApi";
import {toast} from "react-toastify";
import profile2 from "css/image/profile2.png";
import PictureDeleteApi from "utill/api/picture/PictureDeleteApi";
import {PROFILE_EDIT} from "../../modal/content/ModalContent";

export function Profile({
  header,
  setHeader,
  myUserInfo,
  cachedUser,
  useModal1
}) {

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
    const isUserNameChange = header.userName
        !== myUserInfo.userReducer.userName;
    if (isMyProfile && isUserNameChange) {
      const newHeader = {...header, userName: myUserInfo.userReducer.userName}
      cachedUser.addUsers(newHeader);
      setHeader(newHeader);
      return;
    }

    cachedUser.addUsers(header);
  }, [header, myUserInfo.userReducer.userName])
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
    ssbConfirm("t) Are you sure?", "t)  사진을 정말 삭제 하시겠습니까?", true, "t) 삭제",
        "t) 취소",
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
                      t)Replace Image
                    </button>
                  </li>
                  {
                      header.pictureUrl && <li className="list-group-item">
                        <button className="btn btn-upload" onClick={() => {
                          pictureDeleteClickEvent();
                        }}>
                          t)Delete Image
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
                   <Btn text={"안녕"}/></> : <BtnOutLine text="Edit"
                                                       event={clickEditBtnEvent}/>

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
              {/*<div className="no-about">{}</div>*/}

              <article className="sidebar-module" style={{height: "auto"}}>
                <a className="sidebar-header">
                  <h3 className="sidebarHeader__title">
                    <span
                        className="sc-icon sc-icon-like sc-icon-large sidebarHeader__icon"></span>
                    <span className="sidebarHeader__actualTitle">2 likes</span>
                  </h3>
                  <span className="sidebarHeader__more sc-type-h3 sc-text-h4">View all</span>
                </a>
                <div className="sidebarContent">
                  {/*상위*/}
                  <div className="soundBadgeList compact lazyLoadingList">
                    <ul className="sc-list-nostyle sc-clearfix"
                        style={{textAlign: "left", padding: 0}}>
                      <li className="soundBadgeList__item">
                        <div
                            className="soundBadge compact sc-media playlist m-playable">
                          <span
                              className="soundBadge__artwork sc-mr-2x sc-media-image"
                              style={{float: "left", marginRight: `10px`}}>
                            <div
                                className="image image__lightOutline sc-artwork sc-artwork-placeholder-7 m-loaded"
                                style={{height: `50px`, width: `50px`}}>
                              <a href="#">
                            <img style={{backgroundColor: '#fff'}}
                                 className="player_cover_img"
                                 src={profile2} alt="cover"/>
                          </a>
                            </div>
                              <span className="soundBadge__playButton">
                                <a role="button" href=""
                                   className="sc-button-play playButton sc-button sc-button-large"
                                   tabIndex="0" title="Play"
                                   draggable="true">Play</a>
                              </span>
                          </span>

                                <div
                                    className="sc-media-content soundBadge__content">
                              <div
                                  className="soundTitle sc-clearfix sc-hyphenate sc-type-h3 sc-text-h4">
                                <div className="soundTitle__titleContainer">
                                  <div
                                      className="soundTitle__usernameTitleContainer sc-mb-0.5x">
                                    <div
                                        className="sc-type-light sc-text-secondary sc-text-h4 soundTitle__secondary sc-truncate">
                                      <a href="/cindy-lim-82312455"
                                         className="soundTitle__username sc-link-secondary sc-link-light text-decoration-none">
                                        <span
                                            className="soundTitle__usernameText">UaenaCindy</span>
                                      </a>
                                    </div>
                                    <a className="sc-link-primary soundTitle__title sc-link-dark sc-text-h4 text-decoration-none "
                                       href="/cindy-lim-82312455/sets/iu-1"
                                       title="IU 아이유 - Album">
                                      <span className="sc-truncate">IU 아이유 - Album</span>
                                    </a>
                                  </div>
                                  <div
                                      className="soundTitle__additionalContainer sc-ml-1.5x"></div>
                                </div>
                              </div>


                          <ul className="soundStats sc-ministats-group"
                              aria-label="Track stats">  <li title="8,440 plays"
                                                             className="sc-ministats-item">
                              <span
                                  className="sc-ministats sc-ministats-small  sc-ministats-plays sc-text-secondary">
                                <span
                                    className="sc-visuallyhidden">8,440 plays</span>
                                <span
                                    aria-hidden="true">8,440</span>
                              </span>
                            </li>

                            <li title="102 likes" className="sc-ministats-item">
                              <a href="/cindy-lim-82312455/the-thing-i-do-slowly/likes"
                                 rel="nofollow"
                                 className="sc-ministats sc-ministats-small  sc-ministats-likes sc-link-secondary">
                                 <span className="sc-visuallyhidden">View all likes</span><span
                                  aria-hidden="true">102</span>
                              </a>
                            </li>

                            <li title="1 repost" className="sc-ministats-item">
                              <a href="/cindy-lim-82312455/the-thing-i-do-slowly/reposts"
                                 rel="nofollow"
                                 className="sc-ministats sc-ministats-small  sc-ministats-reposts sc-link-secondary">
                                <span className="sc-visuallyhidden">View all reposts</span><span
                                  aria-hidden="true">1</span>
                              </a>
                            </li>
                              <li title="1 comment"
                                  className="sc-ministats-item">
                                  <a href="/cindy-lim-82312455/the-thing-i-do-slowly/comments"
                                     rel="nofollow"
                                     className="sc-ministats sc-ministats-small  sc-ministats-comments sc-link-secondary">
                                   <span className="sc-visuallyhidden">View all comments</span><span
                                      aria-hidden="true">1</span>
                                </a>
                              </li>
                          </ul>

                              <div className="soundBadge__additional">
                                <div className="soundBadge__actions">
                                  <div
                                      className="soundActions sc-button-toolbar soundActions__small">
                                    <div
                                        className="sc-button-group sc-button-group-small">
                                      <button type="button"
                                              className="sc-button-like sc-button-secondary sc-button sc-button-small sc-button-icon sc-button-responsive sc-button-selected"
                                              aria-describedby="tooltip-10265"
                                              tabIndex="0" title="Unlike"
                                              aria-label="Unlike">Liked</button>
                                      <button type="button"
                                              className="sc-button-more sc-button-more sc-button sc-button-small sc-button-icon sc-button-responsive"
                                              tabIndex="0" aria-haspopup="true"
                                              role="button"
                                              aria-owns="dropdown-button-10267"
                                              title="More"
                                              aria-label="More">More</button>
                                    </div>
                                  </div>
                                </div>
                              </div>
                            </div>


                        </div>





                    </li>
                      <li className="soundBadgeList__item">
                        <div
                            className="soundBadge compact sc-media playlist m-playable">
                          <span
                              className="soundBadge__artwork sc-mr-2x sc-media-image"
                              style={{float: "left", marginRight: `10px`}}>
                            <div
                                className="image image__lightOutline sc-artwork sc-artwork-placeholder-7 m-loaded"
                                style={{height: `50px`, width: `50px`}}>
                              <a href="#">
                            <img style={{backgroundColor: '#fff'}}
                                 className="player_cover_img"
                                 src={profile2} alt="cover"/>
                          </a>
                            </div>
                              <span className="soundBadge__playButton">
                                <a role="button" href=""
                                   className="sc-button-play playButton sc-button sc-button-large"
                                   tabIndex="0" title="Play"
                                   draggable="true">Play</a>
                              </span>
                          </span>

                                <div
                                    className="sc-media-content soundBadge__content">
                              <div
                                  className="soundTitle sc-clearfix sc-hyphenate sc-type-h3 sc-text-h4">
                                <div className="soundTitle__titleContainer">
                                  <div
                                      className="soundTitle__usernameTitleContainer sc-mb-0.5x">
                                    <div
                                        className="sc-type-light sc-text-secondary sc-text-h4 soundTitle__secondary sc-truncate">
                                      <a href="/cindy-lim-82312455"
                                         className="soundTitle__username sc-link-secondary sc-link-light text-decoration-none">
                                        <span
                                            className="soundTitle__usernameText">UaenaCindy</span>
                                      </a>
                                    </div>
                                    <a className="sc-link-primary soundTitle__title sc-link-dark sc-text-h4 text-decoration-none "
                                       href="/cindy-lim-82312455/sets/iu-1"
                                       title="IU 아이유 - Album">
                                      <span className="sc-truncate">IU 아이유 - Album</span>
                                    </a>
                                  </div>
                                  <div
                                      className="soundTitle__additionalContainer sc-ml-1.5x"></div>
                                </div>
                              </div>


                          <ul className="soundStats sc-ministats-group"
                              aria-label="Track stats">  <li title="8,440 plays"
                                                             className="sc-ministats-item">
                              <span
                                  className="sc-ministats sc-ministats-small  sc-ministats-plays sc-text-secondary">
                                <span
                                    className="sc-visuallyhidden">8,440 plays</span>
                                <span
                                    aria-hidden="true">8,440</span>
                              </span>
                            </li>

                            <li title="102 likes" className="sc-ministats-item">
                              <a href="/cindy-lim-82312455/the-thing-i-do-slowly/likes"
                                 rel="nofollow"
                                 className="sc-ministats sc-ministats-small  sc-ministats-likes sc-link-secondary">
                                 <span className="sc-visuallyhidden">View all likes</span><span
                                  aria-hidden="true">102</span>
                              </a>
                            </li>

                            <li title="1 repost" className="sc-ministats-item">
                              <a href="/cindy-lim-82312455/the-thing-i-do-slowly/reposts"
                                 rel="nofollow"
                                 className="sc-ministats sc-ministats-small  sc-ministats-reposts sc-link-secondary">
                                <span className="sc-visuallyhidden">View all reposts</span><span
                                  aria-hidden="true">1</span>
                              </a>
                            </li>
                              <li title="1 comment"
                                  className="sc-ministats-item">
                                  <a href="/cindy-lim-82312455/the-thing-i-do-slowly/comments"
                                     rel="nofollow"
                                     className="sc-ministats sc-ministats-small  sc-ministats-comments sc-link-secondary">
                                   <span className="sc-visuallyhidden">View all comments</span><span
                                      aria-hidden="true">1</span>
                                </a>
                              </li>
                          </ul>

                              <div className="soundBadge__additional">
                                <div className="soundBadge__actions">
                                  <div
                                      className="soundActions sc-button-toolbar soundActions__small">
                                    <div
                                        className="sc-button-group sc-button-group-small">
                                      <button type="button"
                                              className="sc-button-like sc-button-secondary sc-button sc-button-small sc-button-icon sc-button-responsive sc-button-selected"
                                              aria-describedby="tooltip-10265"
                                              tabIndex="0" title="Unlike"
                                              aria-label="Unlike">Liked</button>
                                      <button type="button"
                                              className="sc-button-more sc-button-more sc-button sc-button-small sc-button-icon sc-button-responsive"
                                              tabIndex="0" aria-haspopup="true"
                                              role="button"
                                              aria-owns="dropdown-button-10267"
                                              title="More"
                                              aria-label="More">More</button>
                                    </div>
                                  </div>
                                </div>
                              </div>
                            </div>


                        </div>





                    </li>
                      <li className="soundBadgeList__item">
                        <div
                            className="soundBadge compact sc-media playlist m-playable">
                          <span
                              className="soundBadge__artwork sc-mr-2x sc-media-image"
                              style={{float: "left", marginRight: `10px`}}>
                            <div
                                className="image image__lightOutline sc-artwork sc-artwork-placeholder-7 m-loaded"
                                style={{height: `50px`, width: `50px`}}>
                              <a href="#">
                            <img style={{backgroundColor: '#fff'}}
                                 className="player_cover_img"
                                 src={profile2} alt="cover"/>
                          </a>
                            </div>
                              <span className="soundBadge__playButton">
                                <a role="button" href=""
                                   className="sc-button-play playButton sc-button sc-button-large"
                                   tabIndex="0" title="Play"
                                   draggable="true">Play</a>
                              </span>
                          </span>

                                <div
                                    className="sc-media-content soundBadge__content">
                              <div
                                  className="soundTitle sc-clearfix sc-hyphenate sc-type-h3 sc-text-h4">
                                <div className="soundTitle__titleContainer">
                                  <div
                                      className="soundTitle__usernameTitleContainer sc-mb-0.5x">
                                    <div
                                        className="sc-type-light sc-text-secondary sc-text-h4 soundTitle__secondary sc-truncate">
                                      <a href="/cindy-lim-82312455"
                                         className="soundTitle__username sc-link-secondary sc-link-light text-decoration-none">
                                        <span
                                            className="soundTitle__usernameText">UaenaCindy</span>
                                      </a>
                                    </div>
                                    <a className="sc-link-primary soundTitle__title sc-link-dark sc-text-h4 text-decoration-none "
                                       href="/cindy-lim-82312455/sets/iu-1"
                                       title="IU 아이유 - Album">
                                      <span className="sc-truncate">IU 아이유 - Album</span>
                                    </a>
                                  </div>
                                  <div
                                      className="soundTitle__additionalContainer sc-ml-1.5x"></div>
                                </div>
                              </div>


                          <ul className="soundStats sc-ministats-group"
                              aria-label="Track stats">  <li title="8,440 plays"
                                                             className="sc-ministats-item">
                              <span
                                  className="sc-ministats sc-ministats-small  sc-ministats-plays sc-text-secondary">
                                <span
                                    className="sc-visuallyhidden">8,440 plays</span>
                                <span
                                    aria-hidden="true">8,440</span>
                              </span>
                            </li>

                            <li title="102 likes" className="sc-ministats-item">
                              <a href="/cindy-lim-82312455/the-thing-i-do-slowly/likes"
                                 rel="nofollow"
                                 className="sc-ministats sc-ministats-small  sc-ministats-likes sc-link-secondary">
                                 <span className="sc-visuallyhidden">View all likes</span><span
                                  aria-hidden="true">102</span>
                              </a>
                            </li>

                            <li title="1 repost" className="sc-ministats-item">
                              <a href="/cindy-lim-82312455/the-thing-i-do-slowly/reposts"
                                 rel="nofollow"
                                 className="sc-ministats sc-ministats-small  sc-ministats-reposts sc-link-secondary">
                                <span className="sc-visuallyhidden">View all reposts</span><span
                                  aria-hidden="true">1</span>
                              </a>
                            </li>
                              <li title="1 comment"
                                  className="sc-ministats-item">
                                  <a href="/cindy-lim-82312455/the-thing-i-do-slowly/comments"
                                     rel="nofollow"
                                     className="sc-ministats sc-ministats-small  sc-ministats-comments sc-link-secondary">
                                   <span className="sc-visuallyhidden">View all comments</span><span
                                      aria-hidden="true">1</span>
                                </a>
                              </li>
                          </ul>

                              <div className="soundBadge__additional">
                                <div className="soundBadge__actions">
                                  <div
                                      className="soundActions sc-button-toolbar soundActions__small">
                                    <div
                                        className="sc-button-group sc-button-group-small">
                                      <button type="button"
                                              className="sc-button-like sc-button-secondary sc-button sc-button-small sc-button-icon sc-button-responsive sc-button-selected"
                                              aria-describedby="tooltip-10265"
                                              tabIndex="0" title="Unlike"
                                              aria-label="Unlike">Liked</button>
                                      <button type="button"
                                              className="sc-button-more sc-button-more sc-button sc-button-small sc-button-icon sc-button-responsive"
                                              tabIndex="0" aria-haspopup="true"
                                              role="button"
                                              aria-owns="dropdown-button-10267"
                                              title="More"
                                              aria-label="More">More</button>
                                    </div>
                                  </div>
                                </div>
                              </div>
                            </div>


                        </div>





                    </li>
                    </ul>
                  </div>
                </div>


              </article>

                   <article className="sidebar-module">
                <a className="sidebar-header">
                  <h3 className="sidebarHeader__title">
                    <span
                        className="sc-icon sc-icon-following sc-icon-large sidebarHeader__icon"></span>
                    <span className="sidebarHeader__actualTitle  sc-text-h3">5 following</span>
                  </h3>
                  <span className="sidebarHeader__more sc-type-h3 sc-text-h4">View all</span>
                </a>
               <div className="sidebarContent">
                         <div
                             className="soundBadgeList compact lazyLoadingList">
                         </div>
                       </div>









              </article>
              <article className="sidebar-module">




              </article>
              <div className="extraspace"></div>
          </div>
        </span>
          </div>
        </div>
      </div>
  )
}
