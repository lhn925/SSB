import "modal/css/help/help.css"
import "modal/css/join/join.css"
import "modal/css/base/secure.css"
import {useRef, useState} from "react";
import {useTranslation} from "react-i18next";
import {ChangeError} from "utill/function";
import {QueryIdApi} from "utill/api/help/QueryIdApi";
import {Input} from "components/input/Input";
import Help from "modal/content/help/Help";
import {modalActions} from "store/modalType/modalType";
import {helpActions} from "store/helpType/helpType";
import {useDispatch} from "react-redux";

function IdQuery(props) {
  const {t} = useTranslation();
  const [inputs, setInputs] = useState({
    userId: '',
  });

  const dispatch = useDispatch();
  const [result, setResult] = useState(
      {enEmail: null, isQuery: false, userId: null});
  const [errors, setErrors] = useState({
    userId: {message: '', error: false}
  });
  const variable = useRef({
    isDoubleClick: false // 더블 클릭 방지
  })
  const onKeyUp = async (e) => {
    const {value, name} = e.target;
    const input_value = value.split(" ").join("");
    // 공백 체크
    setInputs((inputs) => {
      return {
        ...inputs,
        [name]: input_value
      }
    });
    if (input_value === "") {
      ChangeError(setErrors, name, t(`errorMsg.NotBlank`), true);
    }
    props.RegexCheck(name, input_value, setErrors, t);
  }

  const clickBtnIdQuery = async () => {
    const userId = inputs.userId;

    if (variable.current.isDoubleClick) {
      return;
    }
    variable.current.isDoubleClick = true;
    const response = await QueryIdApi(userId);

    variable.current.isDoubleClick = false;
    if (response.code != 200) {
      if (response.data.errorDetails !== undefined) {
        response.data.errorDetails.map((data) => {
          ChangeError(setErrors, "userId", data.message, true);
        })
        return;
      }
      ChangeError(setErrors, "userId", t(`errorMsg.server`), true);
    } else {
      setResult((result) => {
        return {
          ...result,
          userId: response.data.userId,
          isQuery: true,
          enEmail: response.data.enEmail
        }
      })
    }

  }

  return (
      result.userId != null && result.isQuery ? <Help
              RegexCheck={props.RegexCheck}
              ClickBtnSendCode={props.ClickBtnSendCode}
              ClickBtnAuthCodeCheck={props.ClickBtnAuthCodeCheck}
              closeModal={props.closeModal}
              userId={result.userId}
              enEmail={result.enEmail}
              helpType={props.helpType}/>
          : <IdQueryForm dispatch={dispatch} errors={errors} onKeyUp={onKeyUp}
                         clickBtnIdQuery={clickBtnIdQuery} t={t}/>
  )
}

function IdQueryForm({errors, onKeyUp, t, clickBtnIdQuery, dispatch}) {
  return (
      <div className="card">
        <div className="card-body">
          <h4 className="card-title logo-text mainLogo">{t(
              `msg.common.sky.logo`)}</h4>
          <form id="form">
            <div className="text-center mb-4">
              <span className="form-text">{t(`msg.help.sky.idQueryForm`)}</span>
            </div>
            <div className="form-group">
              <Input name="userId" placeholder={t(`msg.common.sky.id`)}
                     type="text"
                     error={errors.userId.error} message={errors.userId.message}
                     iconClass="form-id" onKeyUp={onKeyUp} t={t}/>
              <div className="d-grid gap-2 col-6 mx-auto">
                <button type="button" onClick={clickBtnIdQuery} id="subBtn"
                        className="btn btn-primary btn-block btn-dark mt-3">{t(
                    `msg.common.sky.nextBtn`)}</button>
                <button type="button" onClick={() =>{
                  dispatch(modalActions.changeType({type: "LOGIN"}));}
                } id="cancelBtn" className="btn btn-outline-info mt-1">{t(
                    `msg.common.sky.cancel`)}</button>
              </div>
            </div>
          </form>
        </div>
        <div className="centered-breadcrumb">
          <div aria-label="breadcrumb">
            <ol className="breadcrumb">
              <li className="breadcrumb-item"><span>{t(
                  `msg.help.sky.idQueryForm2`)}</span></li>
              <li className="breadcrumb-item"><a href="#"
                  onClick={() => {
                    dispatch(modalActions.changeType({type: "HELP"}));
                    dispatch(helpActions.changeType({helpType: "ID"}));
                  }}
                  className="text-decoration-none mainLogo">아이디 찾기</a></li>
            </ol>
          </div>
        </div>
      </div>
  )
}

export default IdQuery;