import {useDispatch, useSelector} from 'react-redux';
import {
  FOLLOWER_IDS,
  FOLLOWING_IDS,
  TRACK_LIKED_IDS, userActions
} from "store/userInfo/userReducers";

const useMyUserInfo = () => {
  const userReducer = useSelector((state) => state?.userReducer);
  const dispatch = useDispatch();

  const isTrackLike = (trackId) => {
    return userReducer.trackLikedIds.includes(trackId);
  }
  const isFollowing = (uid) => userReducer.followingIds.includes(uid);
  const isFollower = (uid) => userReducer.followerIds.includes(uid);

  const updateArrayByType = (ids, type) => {
    dispatch(userActions.setArrayByType({ids, type}));
  };

  /**
   * 사용자 데이터 객체의 키에 따라 동적으로 업데이트할 수 있도록 변경했습니다.
   * Object.keys와 forEach를 사용하여 사용자 데이터 객체의 각 키에 해당하는 액션 크리에이터를 찾아 실행합니다.
   * 배열 타입 데이터(trackLikedIds, followingIds, followerIds)는 별도로 처리합니다.
   * @param userData
   */
  const setUserData = (userData) => {
    const {
      trackLikedIds, followingIds, followerIds,
      id, userId, email, pictureUrl, userName, isLoginBlocked
    } = userData;
    dispatch(userActions.setUid({id}));
    dispatch(userActions.setUserId({userId}));
    dispatch(userActions.setEmail({email}));
    dispatch(userActions.setPictureUrl({pictureUrl}));
    dispatch(userActions.setUserName({userName}));
    dispatch(userActions.setIsLoginBlocked({isLoginBlocked}));
    updateArrayByType(trackLikedIds, TRACK_LIKED_IDS);
    updateArrayByType(followingIds, FOLLOWING_IDS);
    updateArrayByType(followerIds, FOLLOWER_IDS);
  };

  const addArrayValueByType = (id, type) => {
    dispatch(userActions.addArrayValueByType({id: id, type: type}));
  };

  const removeArrayValueByType = (id, type) => {
    dispatch(userActions.removeArrayValueByType({id: id, type: type}));
  };

  return {
    userReducer,
    isTrackLike,
    isFollowing,
    isFollower,
    addArrayValueByType,
    removeArrayValueByType,
    setUserData,
  };
};
export default useMyUserInfo;