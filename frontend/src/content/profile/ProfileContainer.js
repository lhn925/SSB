import {useParams} from "react-router-dom";
import {useEffect, useState} from "react";

const ProfileContainer = ({}) => {
  // 재생 여부
  const params = useParams();

  const [userName, setUserName] = useState(params.userName);


  useEffect(() => {

  },[userName])

  return (
      <>
      </>

  );
};

export default ProfileContainer;