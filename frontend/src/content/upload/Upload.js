import {URL_UPLOAD} from "content/UrlEndpoints";
import Nav from "components/nav/Nav";
import {BtnOutLine} from "components/button/BtnOutLine";
import {ProgressBar} from "components/progressBar/ProgressBar";
import {Btn} from "components/button/Btn";
import "css/upload/upload.css"
import {useEffect, useRef, useState} from "react";
import {TotalLengthApi} from "utill/api/totalLength/TotalLengthApi";
import {TempSaveApi} from "utill/api/upload/TempSaveApi";
import {HttpStatusCode} from "axios";
import {toast} from "react-toastify";
import {v4 as uuidV4} from "uuid";
import {Link} from "react-router-dom";
import iu from "css/image/iu.jpg";
import "css/mention.css";
import {CustomSelect} from "components/customSelect/CustomSelect";
import {Mention, MentionsInput} from "react-mentions";

export const Upload = ({dispatch, uploadInfo, uploadInfoActions}) => {
  const root = "upload";

  const acceptArray = [".mp3", ".flac", ".ogg", ".mp4", ".mpeg", ".wav",
    ".m4a"];
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

      tempTracks.push({
        id: 0,
        title: file.name,
        token: tempToken,
        file: file
      });
      storeTracks.push({
        id: 0,
        title: file.name,
        token: tempToken
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
    if (errorFileCount !== 0) {
      toast.error(
          "파일 중 " + errorFileCount + "개가 지원되지 않습니다. 지원되는 파일 정보를 읽어 보세요");
    }
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

  const updateTracksValue = (key, token, value) => {
    dispatch(uploadInfoActions.updateTracksValue(
        {
          key: key,
          token: token,
          value: value
        }))
  }

  useEffect(() => {

    if (uploadTotalLength === null) {
      getUploadTotalLength().then(r => r);
    }
    if (uploadInfo.tracks.length === 0) {

    } else {
      console.log(uploadInfo.tracks);
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
          {uploadInfo.tracks.length === 0 ? EmptyUploadInfoContent(
                  lengthPercent, uploadTotalLength, clickTrackUploadBtnEvent,
                  changeTrackUploadEvent, acceptArray, trackFileRef, uploadSettings,
                  changePlayListChkEvent, changePrivacyChkEvent) :
              <UploadInfoForm uploadInfo={uploadInfo}/>}
        </div>
      </div>
  )
};

function UploadInfoForm({uploadInfo}) {

  const currentRoot = "BasicInfo";
  const [activeTab, setActiveTab] = useState(currentRoot);
  const genreOptions = ["Option 1", "Option 2", "Option 3"];
  const playListOptions = ["PlayList", "ALBUM"];
  useEffect(() => {
    setActiveTab(currentRoot);
  }, [currentRoot]);

  const [isGenreOpen, setIsGenreOpen] = useState(false);
  const [isPlyTypeOpen, setPlyTypeOpen] = useState(false);
  const [genreSelectedOption, setGenreSelectedOption] = useState(
      genreOptions[0]);
  const [plySelectedOption, setPlySelectedOption] = useState(
      playListOptions[0]);

  const genreToggling = () => setIsGenreOpen(!isGenreOpen);
  const plyToggling = () => setPlyTypeOpen(!isPlyTypeOpen);

  const onGenreOptionClicked = value => () => {
    setGenreSelectedOption(value);
    setIsGenreOpen(false);
  };

  const onPlyOptionClicked = value => () => {
    setPlySelectedOption(value);
    setPlyTypeOpen(false);
  };
  const tabs = [
    {id: "BasicInfo", title: "Basic Info", url: URL_UPLOAD}
  ];

  // 플레이리스트
  if (uploadInfo.isPlayList) {
    return <>
      <ul className="track_info_form_list list-group">
        <li className="list-group-item">
          <div className="editStatus_div">
            <div className="editStatus_info">
              <div className="editStatus_filename basic_font text-start">
                나의 노래 5 (1) (1) 복사본 3.m4a
              </div>
              <div className="editStatus__text basic_font text-end">
                Ready. Click Save to post this track.
              </div>
              <div
                  className="editStatus__text basic_font text-end display-none">32.23MB
                of 49.91MB uploaded
              </div>
            </div>
            <div className="upload_progressBar">
              <ProgressBar percentage="100" width="100" height="5"/>
            </div>
          </div>

          <div className="track_info_form_body">
            <div className="tabs">
              <ul className="nav nav-tabs">
                {tabs.map((tab) => (
                    <li className="nav-item" key={tab.id}>
                      <Link
                          value={tab.id}
                          className={`nav-link link_font_color ${activeTab
                          === tab.id ? "active" : ""}`}
                          to={tab.url}
                      >
                        <h4>{tab.title}</h4>
                      </Link>
                    </li>
                ))}
              </ul>
              {/*tabs ul*/}
            </div>
            <div className="row mt-3">
              <div
                  className="picture-div position-relative d-flex flex-column align-items-center text-center  col-lg-4">
                <div className="image-upload-container">
                  <div className="image-background"
                       style={{backgroundImage: `url(${iu})`}}></div>
                  <div className="upload-btn-wrapper">
                    <button className="btn btn-upload">Upload Image</button>
                  </div>
                </div>
              </div>
              <div
                  className="track_info_form d-flex flex-column col-lg-8 text-start">
                <div className="form-group">
                  <span className="normal_font required_fields">Title</span>
                  <input type="text" className="form-control mb-3"
                         id="title"
                         placeholder="Name your Title"/>
                </div>

                {uploadInfo.isPlayList &&
                    <div className="form-group ">
                      <span className="normal_font">PlayList Type</span>

                      <CustomSelect selectedOption={plySelectedOption} options={playListOptions} onOptionClicked={onPlyOptionClicked}
                                    isOpen={isPlyTypeOpen} toggling={plyToggling}/>
                    </div>}

                <div className="form-group row">
                  <div className="col">
                    <span className="normal_font">Genre</span>

                    <CustomSelect selectedOption={genreSelectedOption}
                                  options={genreOptions}
                                  onOptionClicked={onGenreOptionClicked}
                                  isOpen={isGenreOpen}
                                  toggling={genreToggling}/>
                  </div>
                  <div className="custom_genre_input col">
                    <span className="normal_font">Custom Genre</span>
                    <input type="text" className="form-control mb-3"
                           id="CustomGenre"
                           name="CustomGenre"/>
                  </div>

                </div>
                <div className="form-group">

                  <span className="normal_font">Additional tags</span>

                </div>
                <div className="form-group">
                  <span className="normal_font">Description</span>
                  <textarea className="description form-control mb-3"
                            id="description"
                            name="description"
                            placeholder="Describe your track"></textarea>
                </div>
                <div className="form-group">
                  <span className="normal_font"> Privacy :</span>
                  <div className="form-group mt-1"
                       aria-controls="example-collapse-text2">
                    <label>
                      <input type="radio" name="privacyChk" id="publicChk"
                             className="form-check-input me-1"
                             readOnly/>
                      <span className="normal_font">Public</span>
                    </label>
                    <br/>
                    <span className="form-text ms-3">Anyone will be able to listen to this track.</span>
                  </div>
                  <div className="form-group">
                    <label>
                      <input type="radio" name="privacyChk" id="privateChk"
                             className="form-check-input me-1"
                             readOnly/>

                      <span className="normal_font">Private</span>
                    </label>
                    <br/>
                    <span className="form-text ms-3">If uploaded privately, only the author can play the track.</span>
                  </div>
                </div>
                <div className="form-group">
                  <label>
                    <input type="checkbox" name="isDownloadChk"
                           id="isDownloadChk"
                           className="form-check-input me-1"
                           readOnly/>

                    <span className="normal_font">Enable direct downloads</span>
                    <br/>
                    <span className="form-text">This track will not be available for direct download in the original format it was uploaded.</span>
                    <br/>
                    <span className="form-text error-msg">Distributing content without permission is unlawful.
                      <br/> Make sure you have all necessary rights.</span>
                  </label>
                </div>
              </div>
            </div>
          </div>

          <div className="upload_form_buttons">
            <div className="activeUpload__requiredText text-start"><span
                className="sc-orange sc-text-error">*</span> Required fields
            </div>
            <BtnOutLine text="Cancel"/>
            <Btn text="Save"/>
          </div>
        </li>

      </ul>
    </>;

  } else { // 플레이리스트가 아닐 경우

  }
}

function EmptyUploadInfoContent(lengthPercent, uploadTotalLength,
    clickTrackUploadBtnEvent,
    changeTrackUploadEvent, acceptArray, trackFileRef, uploadSettings,
    changePlayListChkEvent, changePrivacyChkEvent) {
  return <>
    <div className="upload_header card mt-5">
      <div className="card-body row">
        <div className="col-9">
          <div className="text-start mb-1">
            <small>{lengthPercent ? lengthPercent : 0}% of free uploads
              used</small>
          </div>
          <ProgressBar percentage={lengthPercent} width={92}
                       height={6}/>
          <div className="text-start form-text">
            <small>{uploadTotalLength ? uploadTotalLength : 0} of 180
              minutes (0%) used.</small></div>
        </div>
        <div className="col-3 mt-2">
          <BtnOutLine text="Try Next Pro"/>
        </div>
      </div>
    </div>
    <div className="upload_body card mt-2">
      <div className="card-body">
        <h1 className="upload_body_title">Drag and drop your tracks &
          albums here</h1>
        <div className="mt-3">
          <Btn event={clickTrackUploadBtnEvent}
               text="or choose files to upload" width={40}
               name="trackUploadBtn"
               id="trackUploadBtn"/>
          <input type="file"
                 onChange={changeTrackUploadEvent}
                 name="trackUpload"
                 multiple={true}

                 accept={acceptArray.toString()}
                 ref={trackFileRef}
                 id="trackUpload"
                 className="visually-hidden"/>
        </div>
        <div className="form-group mt-1">
          <label>
            <input type="checkbox"
                   checked={uploadSettings.isPlayList}
                   onClick={(e) => changePlayListChkEvent(e)}
                   name="isPlayListChk" id="isPlayListChk"
                   className="form-check-input agreeCheck me-1"
                   readOnly/>
            <span className="checkbox_label">Make a playlist when multiple files are selected</span>
          </label>
        </div>
        <div className="form-group">
          <span className="checkbox_label me-1">Privacy:</span>
          <label className="mb-2">
            <input type="radio" name="privacyChk" id="publicChk"
                   className="me-1"
                   onClick={() => changePrivacyChkEvent(false)}
                   checked={!uploadSettings.isPrivacy}
                   readOnly/>
            <span className="checkbox_label me-2">Public</span>
          </label>
          <label className="mb-2">
            <input type="radio" name="privacyChk" id="privateChk"
                   onClick={() => changePrivacyChkEvent(true)}
                   className="me-1" readOnly/>
            <span className="checkbox_label">Private</span>
          </label>
        </div>
      </div>
    </div>
  </>;
}

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
    return null;
  }
}