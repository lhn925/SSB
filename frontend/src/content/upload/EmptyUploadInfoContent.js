import {ProgressBar} from "components/progressBar/ProgressBar";
import {BtnOutLine} from "components/button/BtnOutLine";
import {Btn} from "components/button/Btn";

export function EmptyUploadInfoContent({lengthPercent, uploadTotalLength,
    clickTrackUploadBtnEvent, uploadSettings,
    changePlayListChkEvent, changePrivacyChkEvent}) {
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
                   defaultChecked={!uploadSettings.isPrivacy}
                   readOnly/>
            <span className="checkbox_label me-2">Public</span>
          </label>
          <label className="mb-2">
            <input type="radio" name="privacyChk" id="privateChk"
                   onClick={() => changePrivacyChkEvent(true)}
                   defaultChecked={uploadSettings.isPrivacy}
                   className="me-1" readOnly/>
            <span className="checkbox_label">Private</span>
          </label>
        </div>
      </div>
    </div>
  </>;
}
