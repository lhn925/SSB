import React, {memo, useCallback, useRef} from 'react';
import {useEffect, useState} from "react";
import {
  ChangeError,
  encodeFileToBase64,
  useToggleableOptions
} from "utill/function";
import {URL_UPLOAD} from "content/UrlEndpoints";
import {ProgressBar} from "components/progressBar/ProgressBar";
import {Link} from "react-router-dom";
import profile2 from "css/image/profile2.png";
import "css/mention.css";
import {CustomSelect} from "components/customSelect/CustomSelect";
import {CustomTagMention} from "components/mention/CustomTagMention";
import {BtnOutLine} from "components/button/BtnOutLine";
import {Btn} from "components/button/Btn";
import {
  GenreTypes,
  PlyTypes, TypeNames,
} from "content/upload/UploadTypes";
import emojiRegex from "emoji-regex";
import {DnDTracksBox} from "content/upload/DnDTracksBox";
import update from 'immutability-helper';
import {useDispatch, useSelector} from "react-redux";

const style = {
  flexWrap: "wrap"
}

const getTracks = (uploadInfo) => {
  return uploadInfo.tracks.map((data) => {
    return data;
  })
}

export function UploadInfoForm({
  updatePlayListValue,
  updateTracksValue,
  updatePlayListObject,
  updateTracksObject,
  addPlayListTagList,
  changeIsPrivacy,
  uploadInfo
}) {
  const [searchTagList, setSearchTagList] = useState({});
  const playListOptions = Object.values(PlyTypes);
  const genreOptions = Object.values(GenreTypes);
  const tabs = [
    {id: "BasicInfo", title: "Basic Info", url: URL_UPLOAD}
  ];

  const commonProps = {
    playListOptions,
    genreOptions,
    tabs,
    searchTagList,
    uploadInfo,
    setSearchTagList,
  };
  // 플레이리스트
  return <ul className="track_info_form_list list-group">
    {
      uploadInfo.isPlayList ? <InfoFormListItem
          index={0}
          updatePlayListObject={updatePlayListObject}
          addPlayListTagList={addPlayListTagList}
          updatePlayListValue={updatePlayListValue}
          changeIsPrivacy={changeIsPrivacy}
          {...commonProps}/> : getTracks(uploadInfo).map((data, index) => (
          <InfoFormListItem
              index={index}
              updateTracksObject={updateTracksObject}
              updateTracksValue={updateTracksValue}
              track={data}
              key={data.token}
              {...commonProps}
          />
      ))
    }
  </ul>;
}

function InfoFormListItem({
  changeIsPrivacy,
  updatePlayListObject,
  updateTracksObject,
  updatePlayListValue,
  updateTracksValue,
  track,
  tabs,
  uploadInfo,
  genreOptions,
  playListOptions,
  addPlayListTagList, searchTagList,
  setSearchTagList,
  index
}) {
  const currentRoot = "BasicInfo";
  const [activeTab, setActiveTab] = useState(currentRoot);
  const acceptArray = [".jpg", ".png", ".jpeg", ".bmp"];

  const isPlayList = uploadInfo.isPlayList;

  const [formValue, setFormValue] = useState(
      isPlayList ? uploadInfo.playList : track);



  const [coverImg, setCoverImg] = useState(profile2);
  const [tracks, setTracks] = useState(getTracks(uploadInfo));


  const {findCard,moveCard} = CardActions(tracks,setTracks);

  const [percentAge, setPercentAge] =
      useState(isPlayList ? uploadInfo.uploadPercent : track.uploadPercent);

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
      updateTracksObject(name, "error", track.token, isError);
      updateTracksObject(name, "message", track.token, message);
    }
  }

  const onBlur = async (e) => {
    const {value, name} = e.target;

    const input_value = value.split(" ").join("");

    // desc 는 1000 자 이하
    // 나머지는 100자 이하
    const lengthLimit = name === "desc" ? 1000 : 100;

    const lengthCheck = input_value.length > lengthLimit;

    const emptyCheck = input_value === "";

    // desc 는 emoji 체크를 안함
    const emojiCheck = name !== "desc";



    // 값 초기화
    if (isPlayList) {
      updatePlayListObject(name, "value", value);
    } else {
      updateTracksObject(name, "value", track.token, value);
    }

    const regex = emojiRegex();
    // title 은 빈공백 x
    if (name === "title" && emptyCheck) {
      changeStoreError(name, isPlayList, "title은 필수 입력 값입니다", true);
      return;
    }

    // 길이제한
    if (!emptyCheck && lengthCheck) {
      changeStoreError(name, isPlayList, lengthLimit + "자 이하로 작성해주세요.", true);
      return;
    }

    if (emojiCheck) {
      const matchAll = input_value.matchAll(regex);
      for (const regex1 of matchAll) {
        if (regex1) {
          changeStoreError(name, isPlayList, "이모지는 안돼요!", true);
          return;
        }
      }
    }

    // error 가 없으면 false
    changeStoreError(name, isPlayList, "", false);
  }

  const genreSelected = !isPlayList ? track.genre : uploadInfo.playList.genre;
  useEffect(() => {
    setActiveTab(currentRoot);
  }, [currentRoot]);
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
  const changeIsDownload = (e) => {
    const checked = e.target.checked
    if (isPlayList) {
      updatePlayListValue("isDownload", checked);
    } else {
      updateTracksValue("isDownload", formValue.token, checked)
    }
  }

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
      console.error("error!!")
    })
    if (isPlayList) {
      updatePlayListValue("coverImgFile", files);
    } else {
      updateTracksValue("coverImgFile", track.token, files);
    }
  }

  useEffect(() => {
    if (isPlayList) {
      updatePlayListValue(plyTypeBox.name, plyTypeBox.selectedOption);
      updatePlayListValue(genreBox.name, genreBox.selectedOption);
      updatePlayListValue(TypeNames.GenreType, genreBox.selectedOption.name);
    } else {
      updateTracksValue(genreBox.name, track.token, genreBox.selectedOption);
      updateTracksValue(TypeNames.GenreType, track.token,
          genreBox.selectedOption.name);
    }
  }, [genreBox.selectedOption, plyTypeBox.selectedOption])

  useEffect(() => {
    const formValue = isPlayList ? uploadInfo.playList : track;
    setPercentAge(isPlayList ? uploadInfo.uploadPercent : track.uploadPercent);
    setFormValue(formValue);
    setTracks(getTracks(uploadInfo));
    if (formValue.coverImgFile !== null) {
      encodeFileToBase64(formValue.coverImgFile[0], setCoverImg).catch(
          () => console.log("error"))
    } else {
      setCoverImg(profile2);
    }
  }, [uploadInfo])
  return <li key={!isPlayList && track.token} className="list-group-item m-1"
             onClick={() => formClickToggleClose(genreBox, plyTypeBox)}>
    <div className="editStatus_div">
      <div className="editStatus_info">
        <div className="editStatus_filename basic_font text-start">
          {percentAge !== 100 ?
              isPlayList ? "Uploading " + uploadInfo.tracks.length + " tracks"
                  : track.title.value : isPlayList
              && "Ready. Click Save to post this playlist."}
        </div>
        <div className="editStatus__text basic_font text-end">
          {percentAge === 100 ?
              !isPlayList && "Ready. Click Save to post this track."
              : percentAge.toFixed(1) + "% uploaded"}
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
                      onClick={clickImgUploadBtnEvent}>Upload Image
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
            <span className="normal_font required_fields">Title</span>
            <input type="text" onBlur={onBlur} className="form-control"
                   name="title"
                   defaultValue={formValue.title.value}
                   placeholder="Name your Title"/>
            <div className="form-text text-danger">
              <small>{formValue.title.error && formValue.title.message}</small>
            </div>
          </div>
          {uploadInfo.isPlayList &&
              <div className="form-group">
                <span className="normal_font">PlayList Type</span>
                <CustomSelect
                    selectBox={plyTypeBox}
                    toggling={() => toggleOptions(plyTypeBox,
                        genreBox)}/>
              </div>}
          <div className="form-group row">
            <div className="col">
              <span className="normal_font">Genre</span>
              <CustomSelect
                  selectBox={genreBox}
                  toggling={() => toggleOptions(genreBox,
                      plyTypeBox)}/>
            </div>
            {
                genreBox.selectedOption.name === GenreTypes.CUSTOM.name && <div
                    className="custom_genre_input col">
                  <span className="normal_font">Custom Genre</span>
                  <input
                      onBlur={onBlur}
                      defaultValue={formValue.customGenre.value}
                      type="text" className="form-control "
                      name="customGenre"/>
                  <div className="form-text text-danger">
                    <small>{formValue.customGenre.error
                        && formValue.customGenre.message}</small>
                  </div>
                </div>
            }
          </div>
          <div className="form-group">

            <span className="normal_font">Additional tags</span>
            <CustomTagMention
                playListType={plyTypeBox}
                addTagListEvent={addPlayListTagList}
                searchTagList={searchTagList}
                setSearchTagList={setSearchTagList}
                setErrors={setErrors}
                storeTagList={uploadInfo.playList.tagList}/>
            <div className="form-text text-danger">
              <small>{errors.tags.error && errors.tags.message}</small>
            </div>
          </div>
          <div className="form-group mb-2">
            <span className="normal_font">Description</span>
            <textarea className="desc form-control"
                      name="desc"
                      defaultValue={formValue.desc.value}
                      onBlur={onBlur}
                      placeholder="Describe your track"></textarea>
            <div className="form-text text-danger">
              <small>{formValue.desc.error && formValue.desc.message}</small>
            </div>
          </div>
          <div className="form-group">
            <span className="normal_font"> Privacy :</span>
            <div className="form-group mt-1"
                 aria-controls="example-collapse-text2">
              <label>
                <input type="radio" name={"privacyChk" + index}
                       className="form-check-input me-1"
                       value={false}
                       onClick={changeIsPrivacyEvent}
                       defaultChecked={!formValue.isPrivacy}
                       readOnly/>
                <span className="normal_font">Public</span>
              </label>
              <br/>
              <span className="form-text ms-3">Anyone will be able to listen to this track.</span>
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
                <span className="normal_font">Private</span>
              </label>
              <br/>
              <span className="form-text ms-3">If uploaded privately, only the author can play the track.</span>
            </div>
          </div>
          <div className="form-group">
            <label>
              <input type="checkbox"
                     checked={formValue.isDownload}
                     onClick={changeIsDownload}
                     name="isDownload"
                     className="form-check-input me-1"
                     readOnly/>

              <span className="normal_font">Enable direct downloads</span>
              <br/>
              <span className="form-text">This track will not be available for direct download in the original format it was uploaded.</span>
              <br/>

              {
                  formValue.isDownload && <span className="form-text error-msg">Distributing content without permission is unlawful.
                      <br/> Make sure you have all necessary rights.</span>
              }

            </label>
          </div>
        </div>
      </div>
    </div>

    {

      isPlayList && tracks.map((data,index) => {
        return <div style={style} key={data.id === 0 ? index : data.id}>
                <DnDTracksBox
                    id={`${data.id}`}
                    track={data}
                    moveCard={moveCard}
                    findCard={findCard}/>
                </div>;
      })
    }
    {/*<Container tracks={getTracks(uploadInfo)}/>*/}

    <div className="upload_form_buttons">
      <div className="activeUpload__requiredText text-start"><span
          className="sc-orange sc-text-error">*</span> Required fields
      </div>
      <BtnOutLine text="Cancel"/>
      <Btn text="Save"/>
    </div>

  </li>;
}

function CardActions(cards,setCards) {
  // Card의 id에 해당하는 Card와 인덱스 리턴
  // {id:1, text:"duckgugong"}이 0번 인덱스면 {id: 1, text:"duckgugong"}, 0 리턴
  const findCard = useCallback(
      (id) => {
        const card = cards.filter((item) => `${item.id}` === id)[0]
        return {
          card,
          index: cards.indexOf(card),
        }
      },
      [cards],
  )
  /*
    Card의 위치 교환.
    state에서 {id: 1,text: 'duckgugong'}가 0번째 인덱스고 {id: 2,text: 'hungry'}가 1번째 인덱스면
    {id:1, text: 'duckgugong'}인 Card를 drag해서 {id:2, text: 'hungry'}에 hover하면
    {id:1, text: 'duckgugong'}가 1번째 인덱스가 되고 {id:2, text: 'hungry'}가 0번째 인덱스가 된다!
  */
  const moveCard = useCallback(
      (id, atIndex) => {
        const {card, index} = findCard(id)
        setCards(
            update(cards, {
              $splice: [
                [index, 1],
                [atIndex, 0, card],
              ],
            }),
        )
      },
      [findCard, cards, setCards],
  )

  return {moveCard, findCard}
}