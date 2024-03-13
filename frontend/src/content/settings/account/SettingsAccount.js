export function SettingsAccount({userInfo}) {
  return (
      <>
        <li className="settings_li_header d-flex">
          <h3 className="settings_h3_title ms-2">가입하신 이메일 주소</h3>
        </li>
        <li className="list-group-item emailImg d-flex align-items-center flex-wrap">
          <h6 className="mb-0">
            <svg xmlns="http://www.w3.org/2000/svg"
                 className="feather feather-globe mr-2 icon-inline">
            </svg>
          </h6>
          <span className="ms-4">{userInfo.email}</span>
        </li>
        <li className="settings_li_header d-flex">
          <h3 className="settings_h3_title ms-2">로그인 아이디</h3>
        </li>
        <li className="list-group-item profileImg d-flex align-items-center flex-wrap">
          <h6 className="mb-0">
            <svg xmlns="http://www.w3.org/2000/svg"
                 className="feather feather-globe mr-2 icon-inline">
            </svg>
          </h6>
          <span className="ms-4">{userInfo.userId}</span>
        </li>
      </>
  )
}