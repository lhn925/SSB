import moment from "moment/moment";
import {modalActions} from "store/modalType/modalType";

export default function Show({t,findUserId,dispatch}) {
  const createdDateTime = moment(findUserId.createdDateTime).format(
      'YYYY-MM-DD HH:mm:ss');
  return (
      <div className="card">
        <div className="card-header">
          가입 한 아이디
        </div>
        <div className="card-body">
          <h5 className="card-title">{findUserId.userId}</h5>
          <p className="card-text">{t(`msg.help.sky.join.date`)}</p>
          <p className="card-text">{createdDateTime}</p>
          <a className="btn btn-primary" onClick={() => dispatch(
              modalActions.changeType({type: "LOGIN"}))}>{t(
              `msg.common.sky.loginBtn`)}</a>
        </div>
      </div>
  );
}