import "css/upload/upload.css"
import {URL_UPLOAD} from "content/UrlEndpoints";
import Nav from "components/nav/Nav";
import {useCallback, useEffect, useRef, useState} from "react";
import {TotalLengthApi} from "utill/api/totalLength/TotalLengthApi";
import {TempSaveApi} from "utill/api/upload/TempSaveApi";
import {HttpStatusCode} from "axios";
import {toast} from "react-toastify";
import {v4 as uuidV4} from "uuid";
import {UploadInfoForm} from "content/upload/UploadInfoForm";
import {EmptyUploadInfoContent} from "content/upload/EmptyUploadInfoContent";
import {convertPictureToFile} from "utill/function";
import {UseUploadActions} from "utill/app/functions";
import {useTranslation} from "react-i18next";
import {acceptTrackArray} from "../../utill/enum/Accept";

export const Upload = ({dispatch, uploadInfo, uploadInfoActions}) => {
  const root = "upload";

  const {t} = useTranslation();

  const tabs = [
    {id: "upload", title: "Upload", url: URL_UPLOAD},
  ];
  // 플레이리스트 업로드 구분
  const [uploadSettings, setUploadSettings] = useState({
    isPlayList: uploadInfo.isPlayList,
    isPrivacy: false // public
  });
  const [uploadTotalLength, setUploadTotalLength] = useState(null);
  const [uploadLimit, setUploadLimit] = useState(180);
  const [lengthPercent, setLengthPercent] = useState(0);

  const {
    removeContextTrack,
    addContextTrack,
    updateContextTrackToken
  } = UseUploadActions();

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

    if (uploadTotalLength > uploadLimit) {
      toast.error(t(`msg.track.upload.limit.reached.text`))
      return;
    }

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

    // 임시 트랙정보 저장
    const tempTracks = [];
    // 스토어 트랙 정보
    const storeTracks = [];

    let sum = 1;

    // 앨범 파일 추출을 위한 라이브러리
    const jsmediatags = window.jsmediatags;

    // 해당 트랙 파일에 이미지 추출
    const getCoverPicture = async (file) => {
      // 새로운 프로미스를 생성하여 반환합니다.
      return new Promise((resolve, reject) => {
        jsmediatags.read(file, {
          onSuccess: function (tag) {
            if (tag.tags.picture) {
              const coverImgFile = convertPictureToFile(tag.tags.picture,
                  file.name);
              // 프로미스의 resolve 함수를 호출하여 결과를 반환합니다.
              resolve(coverImgFile);
            } else {
              resolve(null);
            }
          },
          onError: function (error) {
            // 에러가 발생한 경우, 프로미스의 reject 함수를 호출합니다.
            reject(null);
          }
        });
      });
    }


    for (const file of files) {
      const tempToken = uuidV4();
      const filename = file.name.replace(/\.[^/.]+$/, "");

      // 순서
      const order = uploadInfo.tracks.length + sum;
      sum++;

      // 임시저장 정보
      tempTracks.push({
        id: 0,
        title: filename,
        token: tempToken,
        isPrivacy: isPrivacy,
        isPlayList: isPlayList,
        file: file
      });

      // store 파일정보
      storeTracks.push({
        id: 0,
        title: filename,
        token: tempToken,
        isPrivacy: isPrivacy,
        isPlayList: isPlayList,
        order: order - 1
      });
      if (!isPlayList) {
        const coverPicture = getCoverPicture(file);
        coverPicture.then((picture) => {
          // useContext 추가
          addContextTrack(tempToken, picture);
        }).catch(() => {
        })
      }

    }

    // 임시파일 저장
    await addTracks(storeTracks);
    const loading = toast.loading(t(`msg.common.track.upload`));


    const promises = [];

    tempTracks.map((temp) => {
      const tempToken = temp.token;
      promises.push(
          SaveTempApi(setTracksUploadPercent, tempToken, isPrivacy, isPlayList,
              temp.file));
    })
    Promise.allSettled(promises)
    .then((results) => {
      results.map((result) => {
        if (result.status === 'fulfilled') {
          // 성공시 token 저장
          updateTracksValue("id", result.value.tempToken, result.value.id);
          if (!isPlayList) {
            // 토큰 교체
            updateContextTrackToken(result.value.tempToken, result.value.token);
          }
          updateTracksValue("token", result.value.tempToken,
              result.value.token);

        } else if (result.status === 'rejected') {
          // 실패 시 삭제
          removeTrack(result.reason.message);
        }
      })
    });

    toast.dismiss(loading);
    if (tracks.length !== 0) {
      addTracks({tracks: tracks})
      toast.success(t(`msg.common.upload.success`))
    }
    variable.current.isDoubleClick = false;
    trackFileRef.current.value = '';

  }


  const addTracks = (track) => {
    dispatch(uploadInfoActions.addTracks({
      tracks: track
    }))
  }
  const addSaves = (data) => {
    dispatch(uploadInfoActions.addSaves({
      data: data
    }))
  }

  const setTracksUploadPercent = (token, uploadPercent,abortController) => {

    try {
      dispatch(uploadInfoActions.setTracksUploadPercent(
          {
            token: token,
            uploadPercent: uploadPercent,
          }))
    } catch (error) {
      abortController.abort();
    }
  }
  const removeTrack = (token) => {

    removeContextTrack(token);
    dispatch(uploadInfoActions.removeTrack({token: token}));
  }
  const addTrackTagList = (tags, token) => {
    dispatch(uploadInfoActions.addTrackTagList({token: token, tags: tags}));
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

  const updateTracksObject = (key, subKey, token, value) => {
    dispatch(uploadInfoActions.updateTrackObject(
        {
          key: key,
          subKey: subKey,
          token: token,
          value: value
        }))
  }
  const updatePlayListObject = (key, subKey, value) => {
    dispatch(uploadInfoActions.updatePlayListObject(
        {
          key: key,
          subKey: subKey,
          value: value
        }))
  }

  const updateOrder = (sourceIndex, destIndex) => {
    dispatch(uploadInfoActions.updateOrder(
        {
          sourceIndex: sourceIndex,
          destIndex: destIndex,
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

  const cleanStore = () => {
    dispatch(uploadInfoActions.cleanStore())
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
  }, [])

  useEffect(() => {
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
          {uploadInfo.tracks.length === 0 && uploadInfo.saves.length === 0 ? <EmptyUploadInfoContent
              lengthPercent={lengthPercent}
              uploadLimit={uploadLimit}
              t={t}
              uploadTotalLength={uploadTotalLength}
              clickTrackUploadBtnEvent={clickTrackUploadBtnEvent}
              acceptArray={acceptTrackArray}
              trackFileRef={trackFileRef}
              uploadSettings={uploadSettings}
              changePlayListChkEvent={changePlayListChkEvent}
              changePrivacyChkEvent={changePrivacyChkEvent}
          /> : <UploadInfoForm
              t={t}
              uploadInfo={uploadInfo}
              updateOrder={updateOrder}
              changePlayListChkEvent={changePlayListChkEvent}
              addSaves={addSaves}
              updateTracksObject={updateTracksObject}
              updatePlayListObject={updatePlayListObject}
              updatePlayListValue={updatePlayListValue}
              clickTrackUploadBtnEvent={clickTrackUploadBtnEvent}
              updateTracksValue={updateTracksValue}
              addTagListEvent={uploadSettings.isPlayList ? addPlayListTagList:addTrackTagList}
              removeTrack={removeTrack}
              cleanStore={cleanStore}
              changeIsPrivacy={changeIsPrivacy}/>
          }
        </div>
        {  uploadTotalLength < uploadLimit && <input type="file"
               onChange={changeTrackUploadEvent}
               name="trackUpload"
               multiple={true}
               accept={acceptTrackArray.toString()}
               ref={trackFileRef}
               id="trackUpload"
               className="visually-hidden"/>}
      </div>
  )
};

const SaveTempApi = async (setTracksUploadPercent, tempToken, isPrivacy,
    isPlayList,
    file) => {


  const formData = new FormData();
  formData.append("trackFile", file);
  formData.append("playList", isPlayList);
  formData.append("privacy", isPrivacy);


  const response = await TempSaveApi(setTracksUploadPercent, tempToken, formData);
  const data = response.data;
  if (response.code === HttpStatusCode.Ok) {
    return {
      id: data.id,
      token: data.token,
      title: data.uploadTrackFile.originalFileName,
      isPrivacy: isPrivacy,
      isPlayList: isPlayList,
      tempToken: tempToken,
      file: file
    };
  } else {
    toast.error(data.errorDetails[0].message)
    throw new Error(tempToken);
  }

}