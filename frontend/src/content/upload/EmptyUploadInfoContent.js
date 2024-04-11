import {ProgressBar} from "components/progressBar/ProgressBar";
import {BtnOutLine} from "components/button/BtnOutLine";
import {Btn} from "components/button/Btn";

export function EmptyUploadInfoContent({
  lengthPercent, uploadTotalLength,uploadLimit,
  clickTrackUploadBtnEvent, uploadSettings,
  changePlayListChkEvent, changePrivacyChkEvent, t
}) {

  const isReached = uploadLimit < uploadTotalLength;
  const percent = {
    percent: lengthPercent,
    totalLength: uploadTotalLength ? uploadTotalLength : 0
  };
  return <>
    <div className="upload_header card mt-5">
      <div className="card-body row">
        <div className="col-9">
          <div className="text-start mb-1">
            <small>{isReached ? t(`msg.track.upload.limit.reached`): t(`msg.track.upload.limit.used.text1`, {percent})}</small>
          </div>
          <ProgressBar percentage={lengthPercent} width={92}
                       height={6}/>
          <div className="text-start form-text">
            <small>{t(`msg.track.upload.limit.used.text2`, {percent})}</small></div>
        </div>
        <div className="col-3 mt-2">
          <BtnOutLine text="Try Next Pro"/>
        </div>
      </div>
    </div>
    <div className="upload_body card mt-2">
      <div className="card-body">
        <div className="mt-3">
          <Btn event={clickTrackUploadBtnEvent}
               text={t(`msg.track.upload.choose.btn`)} width={40}
               name="trackUploadBtn"
               id="trackUploadBtn"/>
        </div>
        <div className="form-group mt-1">
          <label>
            <input type="checkbox"
                   checked={uploadSettings.isPlayList}
                   onClick={(e) => changePlayListChkEvent(e)}
                   name="isPlayListChk" id="isPlayListChk"
                   className="form-check-input agreeCheck me-1"
                   readOnly/>
            <span className="checkbox_label">
              {t(`msg.track.upload.make.playlist.selected`)}</span>
          </label>
        </div>
        <div className="form-group">
          <span className="checkbox_label me-1">{t(`msg.track.upload.privacy`)}</span>
          <label className="mb-2">
            <input type="radio" name="privacyChk" id="publicChk"
                   className="me-1"
                   onClick={() => changePrivacyChkEvent(false)}
                   defaultChecked={!uploadSettings.isPrivacy}
                   readOnly/>
            <span className="checkbox_label me-2">{t(`msg.track.upload.privacy.public`)}</span>
          </label>
          <label className="mb-2">
            <input type="radio" name="privacyChk" id="privateChk"
                   onClick={() => changePrivacyChkEvent(true)}
                   defaultChecked={uploadSettings.isPrivacy}
                   className="me-1" readOnly/>
            <span className="checkbox_label">{t(`msg.track.upload.privacy.private`)}</span>
          </label>
        </div>
      </div>
    </div>
  </>;
}
