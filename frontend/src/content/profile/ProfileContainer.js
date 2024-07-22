import {useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import {Profile} from "./Profile";
import FetchUserHeaderApi from "utill/api/profile/FetchUserHeaderApi";
import useCachedUsers from "hoks/cachedUsers/useCachedUsers";
import useMyUserInfo from "../../hoks/user/useMyUserInfo";

const ProfileContainer = () => {
  const {userName} = useParams();
  const [header, setHeader] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const cachedUser = useCachedUsers();
  const myUserInfo = useMyUserInfo();
  const fetchUserHeaderInfo = async () => {
    try {
      setLoading(true);
      let findUser = await cachedUser.fetchUserByUserName(userName);
      let userData = findUser ? findUser[0] : null;
      if (!userData) {
        const response = await FetchUserHeaderApi(userName);
        if (response.code === 200) {
          userData = response.data;
          cachedUser.addUsers(userData);
        } else {
          setError(response.code);
          return;
        }
      }
      setHeader(userData);
    } catch (err) {
      setError('An error occurred while fetching user info.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (userName) {
      fetchUserHeaderInfo();
    }
  }, [userName]);

  return (
      <>
        {loading && <div>Loading...</div>}
        {error && <div>{error}</div>}
        {header && <Profile header={header} myUserInfo={myUserInfo} />}
      </>
  );
};

export default ProfileContainer;