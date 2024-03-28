import "css/upload/upload.css"
import {URL_UPLOAD} from "content/UrlEndpoints";
import Nav from "components/nav/Nav";
import {useEffect, useRef, useState} from "react";
import {TotalLengthApi} from "utill/api/totalLength/TotalLengthApi";
import {TempSaveApi} from "utill/api/upload/TempSaveApi";
import {HttpStatusCode} from "axios";
import {toast} from "react-toastify";
import {v4 as uuidV4} from "uuid";

import {UploadInfoForm} from "content/upload/UploadInfoForm";
import {EmptyUploadInfoContent} from "content/upload/EmptyUploadInfoContent";

export const Upload = ({dispatch, uploadInfo, uploadInfoActions}) => {
  const root = "upload";

  const acceptArray = [".mp3", ".flac", ".ogg", ".mp4", ".mpeg", ".wav",
    ".m4a",".jpg"];
  const tabs = [
    {id: "upload", title: "Upload", url: URL_UPLOAD},
  ];
  // 플레이리스트 업로드 구분
  const [uploadSettings, setUploadSettings] = useState({
    isPlayList: uploadInfo.isPlayList,
    isPrivacy: uploadInfo.isPrivacy // public
  });
  const [uploadTotalLength, setUploadTotalLength] = useState(null);
  const [uploadLimit, setUploadLimit] = useState(180);
  const [lengthPercent, setLengthPercent] = useState(null);

  const trackFileRef = useRef();

  const variable = useRef({
    isDoubleClick: false // 더블 클릭 방지
  })

  // 총 업로드 용량 데이터 요청
  const getUploadTotalLength = async () => {
    const response = await TotalLengthApi();
    if (response.code === 200) {
      const totalLength = response.data.totalLength;
      const limit = response.data.limit;
      // 유저가 총 업로드 한 트랙 길이
      setUploadTotalLength(Number.parseInt(totalLength / 60));
      // track upload limit
      setUploadLimit(limit / 60);
      const lengthPercent = Number.parseInt((totalLength / limit) * 100);
      setLengthPercent(lengthPercent);
    }
  }

  // 파일 btn event
  const clickTrackUploadBtnEvent = () => {
    trackFileRef.current.click();
  }


  // 트랙 임시저장 파일 업로드

  //percentAge 적용 안함
  const changeTrackUploadEvent = async () => {
    const {files} = trackFileRef.current;
    const isPlayList = uploadSettings.isPlayList;
    const isPrivacy = uploadSettings.isPrivacy;
    if (files.length === 0) {
      return;
    }
    if (variable.current.isDoubleClick) {
      return;
    }
    variable.current.isDoubleClick = true;
    const tracks = [];
    let errorFileCount = 0;

    // 임시 트랙정보 저장
    const tempTracks = [];
    // 스토어 트랙 정보
    const storeTracks = [];

    for (const file of files) {
      const tempToken = uuidV4();
      const filename = file.name.replace(/\.[^/.]+$/, "");
      tempTracks.push({
        id: 0,
        title: filename,
        token: tempToken,
        isPrivacy: isPrivacy,
        isPlayList: isPlayList,
        file: file
      });
      storeTracks.push({
        id: 0,
        title: filename,
        token: tempToken,
        isPrivacy: isPrivacy,
        isPlayList: isPlayList,
      });
    }

    // 임시파일 저장
    await addTracks(storeTracks);
    const loading = toast.loading("...track 업로드 중");

    for (const tempTrack of tempTracks) {
      // 임시토큰 발행
      const tempToken = tempTrack.token;
      const track = await SaveTempApi(setUploadPercent, tempToken, isPrivacy,
          isPlayList, tempTrack.file);
      if (!track) { // null 일경우 errors count 증가
        errorFileCount++;
        removeTrack(tempToken);
        continue;
      }
      // 임시저장된 track file 서버에서 준 token 과 아이디로 변경
      updateTracksValue("id", tempToken, track.id);
      updateTracksValue("token", tempToken, track.token);
    }

    toast.dismiss(loading);
    if (tracks.length !== 0) {
      addTracks({tracks: tracks})
      toast.success("업로드가 완료되었습니다.")
    }
    variable.current.isDoubleClick = false;
  }

  const addTracks = (track) => {
    dispatch(uploadInfoActions.addTracks({
      tracks: track
    }))
  }

  const setUploadPercent = (token, uploadPercent) => {
    dispatch(uploadInfoActions.setUploadPercent(
        {
          token: token,
          uploadPercent: uploadPercent
        }))
  }
  const removeTrack = (token) => {
    dispatch(uploadInfoActions.removeTrack({token: token}))
  }


  const addTrackTagList = (tags,token) => {
    dispatch(uploadInfoActions.addTrackTagList({token:token, tags: tags}));
  }
  const addPlayListTagList = (tags) => {
    dispatch(uploadInfoActions.addPlayListTagList({tags: tags}));
  }
  const updateTracksValue = (key, token, value) => {
    dispatch(uploadInfoActions.updateTracksValue(
        {
          key: key,
          token: token,
          value: value
        }))
  }

  const updateTracksObject = (key,subKey, token, value) => {
    dispatch(uploadInfoActions.updateTrackObject(
        {
          key: key,
          subKey:subKey,
          token: token,
          value: value
        }))
  }
  const updatePlayListObject = (key,subKey, value) => {
    dispatch(uploadInfoActions.updatePlayListObject(
        {
          key: key,
          subKey:subKey,
          value: value
        }))
  }


  const updatePlayListValue = (key, value) => {
    dispatch(uploadInfoActions.updatePlayListValue({key: key, value: value}))
  }

  // 플레이 리스트
  const changePlayListChkEvent = (e) => {
    setUploadSettings({...uploadSettings, isPlayList: e.target.checked})
    changeIsPlayList(e.target.checked);
  }

  const changeIsPlayList = (value) => {
    dispatch(uploadInfoActions.changeIsPlayList(
        {isPlayList: value}))
  }
  const changeIsPrivacy = (value) => {
    dispatch(uploadInfoActions.changeIsPrivacy(
        {isPrivacy: value}))
  }

  // privacyChk
  const changePrivacyChkEvent = (value) => {
    setUploadSettings({...uploadSettings, isPrivacy: value})
    changeIsPrivacy(value);
  }

  useEffect(() => {
    if (uploadTotalLength === null) {
      getUploadTotalLength().then(r => r);
    }
    if (uploadInfo.tracks.length === 0) {

    } else {

    }
  }, [uploadInfo])

  return (
      <div className="row justify-content-center">
        <div className="upload_tabs col-lg-10 mb-5">
          <div className="tabs">
            <Nav currentRoot={root} tabs={tabs}/>
          </div>
        </div>

        <div className="upload_content col-lg-8">
          {uploadInfo.tracks.length === 0 ? <EmptyUploadInfoContent
                  lengthPercent={lengthPercent}
                  uploadTotalLength={uploadTotalLength}
                  clickTrackUploadBtnEvent={clickTrackUploadBtnEvent}
                  changeTrackUploadEvent={changeTrackUploadEvent}
                  acceptArray={acceptArray}
                  trackFileRef={trackFileRef}
                  uploadSettings={uploadSettings}
                  changePlayListChkEvent={changePlayListChkEvent}
                  changePrivacyChkEvent={changePrivacyChkEvent}
              /> :<UploadInfoForm
              updateTracksObject={updateTracksObject}
              updatePlayListObject={updatePlayListObject}
              updatePlayListValue={updatePlayListValue}
              updateTracksValue={updateTracksValue}
              addTrackTagList={addTrackTagList}
              addPlayListTagList={addPlayListTagList}
              changeIsPrivacy={changeIsPrivacy}
              uploadInfo={uploadInfo}/>
          }
        </div>
      </div>
  )
};

const SaveTempApi = async (setUploadPercent, tempToken, isPrivacy,
    isPlayList,
    file) => {
  let body = {
    trackFile: file,
    isPlayList: isPlayList,
    isPrivacy: isPrivacy
  }
  const formData = new FormData();
  formData.append("trackFile", file);
  formData.append("isPlayList", isPlayList);
  formData.append("isPrivacy", isPrivacy);

  const response = await TempSaveApi(setUploadPercent, tempToken, body);
  const data = response.data;
  if (response.code === HttpStatusCode.Ok) {
    return {
      id: data.id,
      token: data.token,
      title: data.uploadTrackFile.originalFileName,
      isPrivacy: isPrivacy,
      isPlayList: isPlayList
    };
  } else {
    toast.error(response.data.errorDetails[0].message)
    return null;
  }
}