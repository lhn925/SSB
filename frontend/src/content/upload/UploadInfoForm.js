import React, {useState} from 'react';
import {URL_UPLOAD} from "content/UrlEndpoints";
import {Link, NavLink} from "react-router-dom";
import profile2 from "css/image/profile2.png";
import "css/mention.css";
import {Btn} from "components/button/Btn";

import {USERS_FILE_IMAGE} from "utill/api/ApiEndpoints";
import {InfoFormListItem} from "content/upload/InfoFormListItem";


export function UploadInfoForm({
  updateOrder,
  addSaves,
  updatePlayListValue,
  updateTracksValue,
  clickTrackUploadBtnEvent,
  updatePlayListObject,
  updateTracksObject,
  addTagListEvent,
  changeIsPrivacy,
  uploadInfo,
  removeTrack,
  cleanStore,
  changePlayListChkEvent,
    t
}) {
  const [searchTagList, setSearchTagList] = useState({});

  const tabs = [
    {id: "BasicInfo", title: "Basic Info", url: URL_UPLOAD}
  ];

  const commonProps = {
    tabs,
    searchTagList,
    uploadInfo,
    setSearchTagList,
    updateTracksObject,
    removeTrack,
    addTagListEvent,
    cleanStore,
    addSaves,
    t
  };
  // 플레이리스트
  return <ul className="ul_basic track_info_form_list list-group">
    {
      uploadInfo.isPlayList ? uploadInfo.tracks.length > 0 && <InfoFormListItem
          index={0}
          updateOrder={updateOrder}
          updatePlayListObject={updatePlayListObject}
          updatePlayListValue={updatePlayListValue}
          clickTrackUploadBtnEvent={clickTrackUploadBtnEvent}
          changeIsPrivacy={changeIsPrivacy}
          {...commonProps}/> : getTracks(uploadInfo).map((data, index) => (
          <InfoFormListItem
              index={index}
              updateTracksValue={updateTracksValue}
              track={data}
              key={data.token}
              {...commonProps}
          />
      ))
    }
    {
        uploadInfo.tracks.length === 0 && uploadInfo.saves.length > 0 &&
        getUploadBtn(clickTrackUploadBtnEvent, t, uploadInfo, changePlayListChkEvent)
    }
    {
      uploadInfo.saves.map(save => getSaves(save, t))
    }
  </ul>;
}



const getTracks = (uploadInfo) => {
  return uploadInfo.tracks.map((data) => {
    return data;
  })
}
function getSaves(save, t) {
  return <li key={save.token} className="list-group-item m-1">
    <div className="row activeUpload_saved_container">
      <div className="col-2 ms-2 activeUpload_image">
        <div className="image-background_small"
             style={{
               backgroundImage: `url(${save.coverUrl === null ? profile2
                   : USERS_FILE_IMAGE + save.coverUrl})`
             }}></div>
      </div>
      <div className="text-start ms-3 col-4 activeUpload_savedFields">
        <div className="activeUpload_savedTitle">
          <span>{save.title}</span>
        </div>
        <div className="activeUpload_savedUserName">
          <span className="form-text">{save.userName}</span>
        </div>
        <div className="activeUpload_savedTags">
          <div>
            {
                save.tagList !== null && save.tagList.map(
                    (tag, index) => (
                        <>
                          <NavLink key={index}
                                   className="normal_font me-2 text-decoration-none"
                                   to={`/tags/` + tag.tag}>
                                    <span className="tagContent">{`#`
                                        + tag.tag}</span>
                          </NavLink>
                        </>
                    ))
            }
          </div>
        </div>
        <div className="activeUpload_savedDesc">
          {save.desc}
        </div>
        <div className="activeUpload_savedPrivacy">
                  <span className="mainBg">
                  {t(`msg.track.upload.privacy.` + (save.isPrivacy ?
                      `private` : `public`))}
                  </span>
        </div>
        <div className="activeUpload_savedComplete">
          <span className="form-text">{t(`msg.track.upload.complete`)}</span>
        </div>
        <div className="activeUpload_savedLink">
          <NavLink className="mainColor me-2 text-decoration-none"
                   to={`/` + save.userName + (save.isPlayList && "/sets") + "/"
                       + save.id}>
                    <span>
                           {t(`msg.track.upload.go.to.` + (save.isPlayList ?
                               `playlist` : `track`))}
                    </span>
          </NavLink>
        </div>
      </div>
      <div>
        {
            save.isPlayList && save.tracks.map((track, index) => (
                <div key={index} className="activeUpload_saveTracks">
                  <div className="track-content">
                    <span>{track.value}</span>
                  </div>
                </div>
            ))
        }
      </div>

    </div>
  </li>;
}

function getUploadBtn(clickTrackUploadBtnEvent, t, uploadInfo,
    changePlayListChkEvent) {
  return <li className=" list-group-item  m-1">
    <div className="mt-3">
      <Btn event={clickTrackUploadBtnEvent}
           text={t(`msg.track.upload.choose.btn`)} width={40}
           name="trackUploadBtn"
           id="trackUploadBtn"/>
    </div>
    <div className="form-group mt-1">
      <label>
        <input type="checkbox"
               checked={uploadInfo.isPlayList}
               onClick={(e) => changePlayListChkEvent(e)}
               name="isPlayListChk" id="isPlayListChk"
               className="form-check-input agreeCheck me-1"
               readOnly/>
        <span className="checkbox_label">
              {t(`msg.track.upload.make.playlist.selected`)}</span>
      </label>
    </div>
  </li>;
}
