import {Input} from "components/input/Input";
import {useRef, useState} from "react";
import {useTranslation} from "react-i18next";
import {ChangeError, duplicateCheck, RegexCheck} from "utill/function";
import {BtnOutLine} from "components/button/BtnOutLine";
import UserNameUpdateApi from "utill/api/profile/UserNameUpdateApi";
import {toast} from "react-toastify";
import useMyUserInfo from "hoks/user/useMyUserInfo";

function ProfileEdit({bc, closeModal,changeModalType}) {

  const {t} = useTranslation();
  const {updateUserName,userReducer} = useMyUserInfo();
  const ogUserName = userReducer.userName;
  const [errors, setErrors] = useState({
    userName: {message: '', error: false},
  });
  const variable = useRef({
    isDoubleClick: false // 더블 클릭 방지
  })
  const [inputs, setInputs] = useState({
    userName: userReducer.userName,
  });

  const isStringBlank = (name) => {
    ChangeError(setErrors, name, t(`errorMsg.NotBlank`), true);
    return true;
  }

  const onBlur = async (e) => {
    const {value, name} = e.target;
    const input_value = value.split(" ").join("");
    const isChange = ogUserName !== value;
    // 공백 체크
    setInputs((inputs) => {
      return {
        ...inputs,
        [name]: input_value
      }
    });
    if (input_value === "") {
      return isStringBlank(name);
    }
    const isRegex = RegexCheck(name, input_value, setErrors, t);
    // 정규표현식이 맞고 빈칸이 아닌 경우
    if (!isRegex && isChange) { // 중복 체크
      await duplicateCheck(name, input_value, setErrors, t);
    }
    // 중복 체크 후 중복이면 리턴
  }
  const onClickSubmit = async () => {
    // error 여부 확인
    for (const [key, value] of Object.entries(errors)) {
      if (value.error) {
        return;
      }
    }
    const isChange = ogUserName !== inputs.userName;

    // 공백 여부 확인
    let isBlank = false;
    for (const [key, value] of Object.entries(inputs)) {
      if (value === "") {
        isBlank = isStringBlank(key)
      }
    }
    if (isBlank) {
      return;
    }
    if (variable.current.isDoubleClick) {
      return;
    }
    variable.current.isDoubleClick = true;
    if (!isChange) {
      return;
    }
    let loading = toast.loading("t) 닉네임 변경중");
    const body = {
      userName: inputs.userName,
    }
    const response = await UserNameUpdateApi(body);
    variable.current.isDoubleClick = false;
    const result = response.code !== 200;
    if (result) {

      ChangeError(setErrors, "userName", response.data.errorDetails[0].message, true);
      toast.dismiss(loading);
    } else {
      toast.dismiss(loading);
      toast.success("닉네임 변경 완료");
      updateUserName(inputs.userName);
      closeModal();
    }
  }


  return (
      <div className="card">
        <div className="card-body">
          <h4 className="card-title text-center mb-3">t) Edit your Profile</h4>
          <form>
            <div className="form-group">
              <Input name="userName" placeholder={t(`msg.common.sky.userName`)}
                     type="text"
                     defaultValue={userReducer.userName}
                     error={errors.userName.error}
                     message={errors.userName.message} iconClass="form-name"
                     onBlur={onBlur} t={t}/>
              <small className="form-text">(t) 닉네임은 한달에 한번 바꿀수 있습니다)</small>
            </div>
            <div className="d-grid gap-2 col-6 mx-auto">
              <button type="button" id="subBtn" onClick={onClickSubmit}
                      disabled={ogUserName === inputs.userName || errors.userName.error}
                      className="btn btn-primary btn-block btn-dark mt-3"> t)변경하기
              </button>
              <BtnOutLine id="cancelBtn" text={t(
                  `msg.common.sky.cancel`)} event={closeModal} />
            </div>
          </form>
        </div>
      </div>

  )
}

export default ProfileEdit;