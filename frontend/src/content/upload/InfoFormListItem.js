import emojiRegex from "emoji-regex";
import React, {useEffect, useRef, useState, useTransition} from "react";
import {UseUploadActions, UseUploadValue} from "utill/app/functions";
import profile2 from "css/image/profile2.png";
import {TempRemoveApi} from "utill/api/upload/TempRemoveApi";
import {
  ChangeError, CreatePlayListBody,
  CreateTrackBody, encodeFileToBase64, useToggleableOptions,
  ValueEmojiCheck
} from "utill/function";
import {
  genreOptions,
  GenreTypes,
  playListOptions,
  TypeNames
} from "content/upload/UploadTypes";
import {toast} from "react-toastify";
import {PlayListSaveApi} from "utill/api/upload/PlayListSaveApi";
import {TrackSaveApi} from "utill/api/upload/TrackSaveApi";
import {HttpStatusCode} from "axios";
import {ProgressBar} from "components/progressBar/ProgressBar";
import {Link} from "react-router-dom";
import {CustomSelect} from "components/customSelect/CustomSelect";
import {CustomTagMention} from "components/mention/CustomTagMention";
import {DragDropContext, Draggable, Droppable} from "react-beautiful-dnd";
import {BtnOutLine} from "components/button/BtnOutLine";
import {Btn} from "components/button/Btn";
import {acceptArray} from "../../utill/enum/Accept";

export function InfoFormListItem({
  cleanStore,
  updateOrder,
  changeIsPrivacy,
  updatePlayListObject,
  updateTracksObject,
  updatePlayListValue,
  updateTracksValue,
  track,
  tabs,
  uploadInfo,
  addTagListEvent, searchTagList,
  setSearchTagList,
  index,
  removeTrack,
  clickTrackUploadBtnEvent,
  addSaves,
  t,
}) {
  const regex = emojiRegex();
  const currentRoot = "BasicInfo";

  const msgDefaultPath = `msg.track.upload.`;

  const [activeTab, setActiveTab] = useState(currentRoot);

  const [isPlayList, setIsPlayList] = useState(uploadInfo.isPlayList);

  const [formValue, setFormValue] = useState(
      isPlayList ? uploadInfo.playList : track);

  const {
    updateContextPly,
    updateContextTrackFile,
    getTrackFile,
    getPlyFile
  } = UseUploadActions();

  const contextValue = UseUploadValue();

  const [coverImg, setCoverImg] = useState(profile2);

  const [tracks, setTracks] = useState(getTracks(uploadInfo));

  const [percentAge, setPercentAge] =
      useState(isPlayList ? uploadInfo.uploadPercent : formValue.uploadPercent);

  const [errors, setErrors] = useState({
    tags: {message: '', error: false},
    coverImgFile: {message: '', error: false}
  });

  const coverImgFileRef = useRef();

  const changeStoreError = (name, isPlayList, message, isError) => {
    if (isPlayList) {
      updatePlayListObject(name, "error", isError);
      updatePlayListObject(name, "message", message);
    } else {
      updateTrackError(name, formValue.token, isError, message);
    }
  }

  const updateTrackError = (name, token, isError, message) => {
    updateTracksObject(name, "error", token, isError);
    updateTracksObject(name, "message", token, message);
  }

  const removeTrackHandler = (token) => {
    try {
      // length 가 0 이면 초기화
      if (uploadInfo.tracks.length === 1) {
        updateContextPly(null);
        cleanStore();
      }
      removeTrack(token);
    } catch (error) {
      console.error("removeTrackHandler")
    }
  }

  // 삭제 이벤트
  const removeTrackBtnClickEvent = (token, id) => {
    const isConfirm = window.confirm(t(msgDefaultPath + `cancel`));
    if (isConfirm) {
      removeTrackHandler(token);
      if (id !== 0) {
        const body = {tempTrackDeleteList: [{id: id, token: token}]};
        const response = TempRemoveApiHandler(body);
        if (response.code !== 200) {
        }
      }
    }
  }
  // 플레이 리스트 삭제 이벤트
  const cancelPlyBtnClickEvent = () => {
    const isConfirm = window.confirm(t(msgDefaultPath + `cancel`));
    if (isPlayList && isConfirm) {
      const tracks = uploadInfo.tracks;
      cleanStore();
      const removeList = tracks.filter((track) => track.id !== 0);
      // context 초기화
      updateContextPly(null);
      const body = {tempTrackDeleteList: removeList};
      if (removeList.length > 0) {
        const response = TempRemoveApiHandler(body);
        if (response.code !== 200) {
        }
      }
    }
  }

  const TempRemoveApiHandler = (body) => {
    return TempRemoveApi(body);
  }
  const onBlurPlyTitle = async (e) => {
    const {value, name, dataset} = e.target;
    const input_value = value.split(" ").join("");
    const lengthLimit = 100;
    const emptyCheck = input_value === "";
    const token = dataset.id;
    const limit = {limit: lengthLimit};
    const lengthCheck = input_value.length > lengthLimit;
    // desc 는 emoji 체크를 안함
    const emojiCheck = name !== "desc";

    updateTracksObject(name, "value", token, value);
    if (emptyCheck) {
      updateTrackError(name, token, true, t(`errorMsg.NotBlank`))
      return false;
    }
    // 길이제한
    if (!emptyCheck && lengthCheck) {
      updateTrackError(name, token, true, t(`errorMsg.input.limit`, {limit}))
      return false;
    }

    const check = ValueEmojiCheck(emojiCheck, input_value, regex);
    // 이모지 체크후 있으면 error
    if (!check) {
      updateTrackError(name, token, true, t(`errorMsg.input.valid`, {limit}))
    }

    // error 가 없으면 false
    updateTrackError(name, token, false, "");
  }

  const saveBtnClickEvent = async () => {
    const title = formValue.title;
    const tagList = formValue.tagList;
    const genreType = formValue.genreType;
    const genre = formValue.genre;
    const customGenre = formValue.customGenre;
    const desc = formValue.desc;

    // 공통적인 값 체크
    if (errors.tags.error || tagList.length > 30) {
      ChangeError(setErrors, "tags", t(msgDefaultPath + `tag.limit`), true);
      return;
    }
    if (formValue.title.value === "") {
      changeStoreError("title", isPlayList, t(`errorMsg.NotBlank`), true);
      return;
    }

    // 값 체크
    if ((genreType === GenreTypes.CUSTOM.name && customGenre.error)
        || desc.error || title.error) {
      return;
    }

    const loading = toast.loading(t(msgDefaultPath + `info.loading`));
    const form = new FormData();

    const coverImgFile = isPlayList ? getPlyFile(contextValue) : getTrackFile(
        contextValue, formValue.token);

    if (isPlayList && uploadInfo.isSuccess) {
      const trackInfoList = [];
      uploadInfo.tracks.map((track, index) => {
        if (track.id === 0) {
          // 만약 isSuccess 인데 track id가 0인 경우가 있을 경우
          toast.dismiss(loading);
          toast.error(t(msgDefaultPath + `not.success`));
          return;
        }
        // 필수 정보 확인
        if (track.title.error) {
          toast.dismiss(loading);
          return;
        }
        // 플레이리스트 일부 정보를 해당 플레이리스트에 삽입
        trackInfoList.push(CreateTrackBody({
          ...track,
          genreType: genreType,
          genre: genre,
          customGenre: customGenre,
          isDownload: formValue.isDownload,
          order: index
        }));
      })

      const playListBody = CreatePlayListBody(formValue);

      playListBody.playListTrackInfoDtoList = trackInfoList;

      form.append("playListSettingSaveDto",
          new Blob([JSON.stringify(playListBody)], {type: "application/json"}));

      if (coverImgFile) {
        form.append("coverImgFile", coverImgFile[0]);
      }

      const response = await PlayListSaveApi(form);
      toast.dismiss(loading);
      const data = response.data;
      const code = response.code;
      TrackCommonErrorTry(code, data, isPlayList, data.errorDetails,
          playListBody);
      return;
    } else if (!isPlayList && formValue.id !== 0 && formValue.isSuccess) {
      const body = CreateTrackBody(formValue);

      if (coverImgFile !== undefined) {
        form.append("coverImgFile", coverImgFile[0]);
      }
      form.append("trackInfoSaveReqDto",
          new Blob([JSON.stringify(body)], {type: "application/json"}));
      const response = await TrackSaveApi(form);

      toast.dismiss(loading);
      const data = response.data;
      const code = response.code;
      TrackCommonErrorTry(code, data, isPlayList, data.errorDetails);
      // 입력할 파일이 없는 경우 403
      // 임시파일이 존재하지 않는 경우
      return;

    }
    toast.error(t(msgDefaultPath + `not.success`));
    toast.dismiss(loading);

  }

  function TrackCommonErrorTry(code, data, isPlayList, errorDetails,
      playListBody) {

    if (code === HttpStatusCode.Ok) {
      data.isPrivacy = formValue.isPrivacy;
      data.isPlayList = isPlayList;
      data.tagList = formValue.tagList;
      data.desc = formValue.desc.value;

      if (isPlayList) {
        data.tracks = uploadInfo.tracks.map(track => track.title);
        cleanStore();
      } else {
        removeTrackHandler(formValue.token);
      }
      addSaves(data);
      return;
    }

    // 파일이 없는 경우
    if (code === HttpStatusCode.Forbidden) {
      if (isPlayList) {
        // 웹에 있는 store 정보 전부 삭제
        cleanStore();
        toast.error(errorDetails[0].message);
        return;

      }
      // 웹에 있는 트랙 정보 삭제
      removeTrackHandler(formValue.token);
      toast.error(errorDetails[0].message);
      return;
    }

    if (code === HttpStatusCode.BadRequest) {
      if (isPlayList) {
        errorDetails.map(error => {
          const field = error.field;

          if (field === undefined) {
            toast.error(error.message);
            return;
          }
          // playList 에 Info 오류 일 경우
          if (field === "title" || field === "genre" || field === "desc") {
            const name = field === "genre" ? "customGenre" : field;
            updatePlayListObject(name, "error", true);
            updatePlayListObject(name, "message", error.message);
            return;
          }
          const fieldDto = field.slice(0, 24);
          // track 리스트에 에러 일 경우
          if (fieldDto === "playListTrackInfoDtoList") {
            const firstIndex = field.indexOf("[") + 1;
            const lastIndex = field.indexOf("]") + 1;
            // Error Type 추출
            const errorType = field.slice(lastIndex + 1);
            // 배열 인덱스 추출
            const index = Number.parseInt(
                field.slice(firstIndex, firstIndex + 1)); // 배열 인덱스 추출;
            const trackInfo = playListBody.playListTrackInfoDtoList[index];
            if (errorType === "title") {
              updateTrackError(errorType, trackInfo.token, true, error.message);
            }
          }
        })
        return;
      }
      if (errorDetails[0].field === undefined) {
        toast.error(errorDetails[0].message);
      } else {
        let name = errorDetails[0].field;
        if (name === "genre") {
          name = "customGenre";
        }
        updateTrackError(name, formValue.token, true, errorDetails[0].message);
      }
    }
  }

  const onBlur = async (e) => {
    const {value, name} = e.target;

    const input_value = value.split(" ").join("");

    // desc 는 1000 자 이하
    // 나머지는 100자 이하
    const lengthLimit = name === "desc" ? 1000 : 100;

    const limit = {limit: lengthLimit};
    const lengthCheck = input_value.length > lengthLimit;

    const emptyCheck = input_value === "";

    // desc 는 emoji 체크를 안함
    const emojiCheck = name !== "desc";

    // 값 초기화
    if (isPlayList) {
      updatePlayListObject(name, "value", value);
    } else {
      updateTracksObject(name, "value", formValue.token, value);
    }

    // title 은 빈공백 x
    if (name === "title" && emptyCheck) {
      changeStoreError(name, isPlayList, t(`errorMsg.NotBlank`), true);
      return;
    }

    // 길이제한
    if (!emptyCheck && lengthCheck) {
      changeStoreError(name, isPlayList, t(`errorMsg.input.limit`, {limit}),
          true);
      return;
    }

    const check = ValueEmojiCheck(emojiCheck, input_value, regex);
    // 이모지 체크후 있으면 error
    if (!check) {
      changeStoreError(name, isPlayList, t(`errorMsg.input.valid`, {limit}),
          true);
      return;
    }
    // error 가 없으면 false
    changeStoreError(name, isPlayList, "", false);
  }

  const genreSelected = !isPlayList ? track.genre : uploadInfo.playList.genre;
  // 재생 목록 유형 선택을 위한 커스텀 훅 사용
  const plyTypeBox = useToggleableOptions(TypeNames.PlyTypes,
      playListOptions, uploadInfo.playList.playListType);
  // 장르 선택을 위한 커스텀 훅 사용
  const genreBox = useToggleableOptions(TypeNames.MusicTypes, genreOptions,
      genreSelected);
  //
  const toggleOptions = (target, other) => {
    target.toggleOptions();
    if (other.isOpen) {
      other.setIsOpen(false);
    }
  };

  //이외의 영역 클릭시 닫힘
  const formClickToggleClose = (genre, playList) => {
    if (genre.isOpen) {
      genre.setIsOpen(false);
    } else if (playList.isOpen) {
      playList.setIsOpen(false);
    }
  };

  const changeIsPrivacyEvent = (e) => {
    const value = e.target.value === "true";
    if (isPlayList) {
      changeIsPrivacy(value);
    } else {
      updateTracksValue("isPrivacy", formValue.token, value)
    }
  }
  const changeIsDownloadEvent = (e) => {
    const checked = e.target.checked
    if (isPlayList) {
      updatePlayListValue("isDownload", checked);
    } else {
      updateTracksValue("isDownload", formValue.token, checked)
    }
  }

  useEffect(() => {
    setActiveTab(currentRoot);
  }, [currentRoot]);

  // 파일 btn event
  const clickImgUploadBtnEvent = () => {
    coverImgFileRef.current.click();
  }
  const changeCoverImgUploadEvent = () => {
    const {files} = coverImgFileRef.current;
    if (files.length === 0) {
      return;
    }
    encodeFileToBase64(files[0], setCoverImg).catch(() => {
      setCoverImg(profile2);
    })

    if (isPlayList) {
      updateContextPly(files);
    } else {
      updateContextTrackFile(formValue.token, files);
    }
  }
  useEffect(() => {
    if (isPlayList) {
      updatePlayListValue(plyTypeBox.name, plyTypeBox.selectedOption);
      updatePlayListValue(genreBox.name, genreBox.selectedOption);
      updatePlayListValue(TypeNames.GenreType, genreBox.selectedOption.name);
    } else {
      updateTracksValue(genreBox.name, formValue.token,
          genreBox.selectedOption);
      updateTracksValue(TypeNames.GenreType, formValue.token,
          genreBox.selectedOption.name);
    }
  }, [genreBox.selectedOption, plyTypeBox.selectedOption])

  useEffect(() => {
    setIsPlayList(uploadInfo.isPlayList);
    const formValue = isPlayList ? uploadInfo.playList : track;
    setPercentAge(isPlayList ? uploadInfo.uploadPercent : track.uploadPercent);
    setFormValue(formValue);
    setTracks(getTracks(uploadInfo));
  }, [uploadInfo])
  useEffect(() => {
    const coverImgFile = uploadInfo.isPlayList ? getPlyFile(contextValue)
        : getTrackFile(contextValue, formValue.token);
    if (coverImgFile !== null && coverImgFile !== undefined) {
      encodeFileToBase64(coverImgFile[0], setCoverImg).catch(
          () => console.log("error"))
    } else {
      setCoverImg(profile2);
    }
  }, [contextValue])

  return <li key={!isPlayList && formValue.token}
             className="list-group-item m-1"
             onClick={() => formClickToggleClose(genreBox, plyTypeBox)}>
    <div className="editStatus_div">
      <div className="editStatus_info">
        <div className="editStatus_filename basic_font text-start">
          {percentAge !== 100 ?
              isPlayList ? t(msgDefaultPath + `uploading.tracks`,
                      {length: uploadInfo.tracks.length})
                  : formValue.title.value : isPlayList
              && t(msgDefaultPath + `ready.click.ply`)}
        </div>
        <div className="editStatus__text basic_font text-end">
          {percentAge === 100 ?
              !isPlayList && t(msgDefaultPath + `ready.click`)
              : t(msgDefaultPath + `uploaded`,
                  {percent: percentAge.toFixed(1)})}
        </div>
      </div>
      <div className="upload_progressBar">
        <ProgressBar percentage={percentAge} width="100" height="5"/>
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
                 style={{backgroundImage: `url(${coverImg})`}}></div>
            <div className="upload-btn-wrapper">
              <button className="btn btn-upload"
                      onClick={clickImgUploadBtnEvent}>
                {t(`msg.common.image.btn`)}
              </button>
              <input type="file"
                     name="coverImgFile"
                     ref={coverImgFileRef}
                     onChange={changeCoverImgUploadEvent}
                     multiple={false}
                     accept={acceptArray.toString()}
                     className="visually-hidden"/>
            </div>

          </div>
        </div>
        <div
            className="track_info_form d-flex flex-column col-lg-8 text-start">
          <div className="form-group mb-2">
            <span className="normal_font required_fields">
                {t(`msg.common.title`)}
            </span>
            <input type="text" onBlur={onBlur}
                   className={"form-control " + (formValue.title.error
                       && "border-danger")}
                   name="title"
                   defaultValue={formValue.title.value}
                   placeholder={t(msgDefaultPath+`name.your.title`)}/>
            <div className="form-text text-danger">
              <small>{formValue.title.error && formValue.title.message}</small>
            </div>
          </div>
          {uploadInfo.isPlayList &&
              <div className="form-group">
                <span className="normal_font">
                  {t(`msg.common.playlist.type`)}
                </span>
                <CustomSelect
                    selectBox={plyTypeBox}
                    toggling={() => toggleOptions(plyTypeBox,
                        genreBox)}/>
              </div>}
          <div className="form-group row">
            <div className="col">
              <span className="normal_font">
                {t(`msg.common.genre`)}
              </span>
              <CustomSelect
                  selectBox={genreBox}
                  toggling={() => toggleOptions(genreBox,
                      plyTypeBox)}/>
            </div>
            {
                genreBox.selectedOption.name === GenreTypes.CUSTOM.name && <div
                    className="custom_genre_input col">
                  <span className="normal_font">{t(
                      `msg.common.custom.genre`)}</span>
                  <input
                      onBlur={onBlur}
                      defaultValue={formValue.customGenre.value}
                      type="text"
                      className={"form-control " + (formValue.customGenre.error
                          && "border-danger")}
                      name="customGenre"/>
                  <div className="form-text text-danger">
                    <small>{formValue.customGenre.error
                        && formValue.customGenre.message}</small>
                  </div>
                </div>
            }
          </div>
          <div className="form-group">

            <span className="normal_font">{t(
                msgDefaultPath + `additional.tags`)}</span>
            <CustomTagMention
                t={t}
                playListType={plyTypeBox}
                addTagListEvent={addTagListEvent}
                searchTagList={searchTagList}
                setSearchTagList={setSearchTagList}
                setErrors={setErrors}
                token={!isPlayList && formValue.token}
                storeTagList={formValue.tagList}/>
            <div className="form-text text-danger">
              <small>{errors.tags.error && errors.tags.message}</small>
            </div>
          </div>
          <div className="form-group mb-2">
            <span className="normal_font">{t(
                msgDefaultPath + `description`)}</span>
            <textarea className={"desc form-control " + (formValue.desc.error
                && " border-danger")}
                      name="desc"
                      defaultValue={formValue.desc.value}
                      onBlur={onBlur}
                      placeholder={t(msgDefaultPath + `describe.track`)}></textarea>
            <div className="form-text text-danger">
              <small>{formValue.desc.error && formValue.desc.message}</small>
            </div>
          </div>
          <div className="form-group">
            <span className="normal_font"> {t(
                msgDefaultPath + `privacy`)}</span>
            <div className="form-group mt-1"
                 aria-controls="example-collapse-text2">
              <label>
                <input type="radio" name={"privacyChk" + index}
                       className="form-check-input me-1"
                       value={false}
                       onClick={changeIsPrivacyEvent}
                       defaultChecked={!formValue.isPrivacy}
                       readOnly/>
                <span className="normal_font">{t(
                    msgDefaultPath + `privacy.public`)}</span>
              </label>
              <br/>
              <span className="form-text ms-3">{t(
                  msgDefaultPath + `privacy.public.ex`)}</span>
            </div>
            <div className="form-group">
              <label>
                <input
                    type="radio" name={"privacyChk" + index}
                    value={true}
                    onClick={changeIsPrivacyEvent}
                    defaultChecked={formValue.isPrivacy}
                    className="form-check-input me-1"
                    readOnly/>
                <span className="normal_font">{t(
                    msgDefaultPath + `privacy.private`)}</span>
              </label>
              <br/>
              <span className="form-text ms-3">{t(
                  msgDefaultPath + `privacy.private.ex`)}</span>
            </div>
          </div>
          <div className="form-group">
            <label>
              <input type="checkbox"
                     checked={formValue.isDownload}
                     onClick={changeIsDownloadEvent}
                     name="isDownload"
                     className="form-check-input me-1"
                     readOnly/>

              <span className="normal_font">{t(
                  msgDefaultPath + `enable.downloads`)}</span>
              <br/>
              <span className="form-text">{t(
                  msgDefaultPath + `downloads.ex`)}</span>
              <br/>

              {
                  formValue.isDownload && <span className="form-text error-msg">
                {t(msgDefaultPath + `downloads.distributing`)}
                  </span>
              }

            </label>
          </div>
        </div>
      </div>
    </div>

    {
        isPlayList && <DragDropContext
            onDragEnd={(e) => handleOnDragEnd(e, updateOrder)}>
          <Droppable droppableId="droppable-songs">
            {(provided) => getDragAndDrop(provided, tracks, onBlurPlyTitle,
                removeTrackBtnClickEvent)}
          </Droppable>
        </DragDropContext>
    }

    {isPlayList && <div className="upload_form_add_buttons">
      <BtnOutLine event={clickTrackUploadBtnEvent} text={t(msgDefaultPath + `add.more`)}/>
    </div>
    }
    <div className="upload_form_buttons">
      <div className="activeUpload__requiredText text-start"><span
          className="sc-orange sc-text-error">*</span> {t(msgDefaultPath + `required.fields`)}
      </div>
      <BtnOutLine event={() => !isPlayList ?
          removeTrackBtnClickEvent(formValue.token, formValue.id)
          : cancelPlyBtnClickEvent()}
                  text={t(`msg.common.sky.cancel`)}/>
      <Btn text={t(`msg.common.sky.save`)} event={saveBtnClickEvent}/>
    </div>

  </li>;
}

function getDragAndDrop(provided, tracks, onBlurPlyTitle,
    removeTrackBtnClickEvent) {
  return <div {...provided.droppableProps} className="mt-3"
              ref={provided.innerRef}>
    {tracks.map((track, index) => (
        <Draggable key={track.token}
                   draggableId={track.token} index={index}>
          {(provided) => (
              <>
                <div ref={provided.innerRef}{...provided.draggableProps}
                     className="playlist-item">
                  <ProgressBar height={3} width={100}
                               percentage={track.uploadPercent}/>
                  <div className="track-content" {...provided.dragHandleProps}>
                    <span {...provided.dragHandleProps}
                          className="drag-handle">:::</span>
                    <input data-id={track.token} type="text"
                           name="title"
                           onBlur={onBlurPlyTitle}
                           className={"form-control track-title "
                               + (track.title.error && " border-danger")}
                           defaultValue={track.title.value}/>
                    <BtnOutLine
                        event={() => removeTrackBtnClickEvent(track.token,
                            track.id)}
                        text={"x"}/>
                  </div>
                  <div className="ply_title_error text-start text-danger">
                    <small>
                      {track.title.error && track.title.message}
                    </small>
                  </div>
                </div>
              </>
          )}
        </Draggable>
    ))}
    {provided.placeholder}
  </div>;
}

function handleOnDragEnd(result, updateOrder) {
  if (!result.destination) {
    return;
  }
  updateOrder(result.source.index, result.destination.index);
}

const getTracks = (uploadInfo) => {
  return uploadInfo.tracks.map((data) => {
    return data;
  })
}

