import {useDispatch, useSelector} from 'react-redux';
import {
  FOLLOWER_IDS,
  FOLLOWING_IDS,
  TRACK_LIKED_IDS, userActions
} from "store/userInfo/userReducers";
import useCachedUsers from "../cachedUsers/useCachedUsers";

const useMyUserInfo = () => {
  const dispatch = useDispatch();

  const userReducer =  useSelector((state) => state?.userReducer);
  // const currentAuth = useAuth();
  const cachedUser = useCachedUsers();

  const isTrackLike = (trackId) => {
    return userReducer?.trackLikedIds.includes(trackId);
  }
  const isFollowing = (uid) => userReducer?.followingIds.includes(uid);
  const isFollower = (uid) => userReducer?.followerIds.includes(uid);

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
        userMyInfo,
      id,  pictureUrl, userName, trackTotalCount
    } = userData;


    dispatch(userActions.setUid({id}));
    dispatch(userActions.setPictureUrl({pictureUrl}));
    dispatch(userActions.setUserName({userName}));
    dispatch(userActions.setUserId({userId:userMyInfo.userId}));
    dispatch(userActions.setEmail({email:userMyInfo.email}));
    dispatch(userActions.setIsLoginBlocked({isLoginBlocked:userMyInfo.isLoginBlocked}));
    updateArrayByType(userMyInfo.trackLikedIds, TRACK_LIKED_IDS);
    updateArrayByType(userMyInfo.followingIds, FOLLOWING_IDS);
    updateArrayByType(userMyInfo.followerIds, FOLLOWER_IDS);
  };

  const updatePictureUrl = (pictureUrl) => {
    updateByKey("pictureUrl", pictureUrl).catch(e => console.log(e))
    dispatch(userActions.setPictureUrl({pictureUrl}));
  }

  const updateUserName = (userName) => {
    updateByKey("userName", userName).catch(e => console.log(e));
    dispatch(userActions.setUserName({userName}));
  }

  const updateIsLoginBlocked = async (isLoginBlocked) => {
    const userArray = await cachedUser.fetchUsers(userReducer.id);
    const user = userArray[0];
    const updatedUser = Object.assign({}, user, {
      userMyInfo: Object.assign({}, user.userMyInfo, {
        isLoginBlocked: isLoginBlocked,
      }),});

    dispatch(userActions.setIsLoginBlocked({isLoginBlocked:isLoginBlocked}));
    cachedUser.addUsers(updatedUser);
  }


  const updateByKey = async (key,value) => {
    const userArray = await cachedUser.fetchUsers(userReducer.id);
    const user = userArray[0];
    const updatedUser = {
      ...user,
      [key]: value,
    };
    cachedUser.addUsers(updatedUser);
  }
  const addArrayValueByType = (id, type) => {
     const copyArray = [...userReducer[type]];

    copyArray.push(id);
    updateUserMyInfo(type, copyArray).catch(e => console.error(e));
    dispatch(userActions.addArrayValueByType({values: copyArray, type: type}));
  };

  const removeArrayValueByType = (id, type) => {
    const copyArray = [...userReducer[type]];
    const removeArray = copyArray.filter(val => val !== id);
    updateUserMyInfo(type, removeArray).catch(e => console.error(e));
    dispatch(userActions.removeArrayValueByType({values: removeArray, type: type}));
  };

  const updateUserMyInfo = async (key,value) => {
    const userArray = await cachedUser.fetchUsers(userReducer.id);
    const user = userArray[0];
    const updatedUser = Object.assign({}, user, {
      userMyInfo: Object.assign({}, user.userMyInfo, {
        [key]: value,
      }),});

    console.log(updatedUser);
    cachedUser.addUsers(updatedUser);
  }

  return {
    userReducer,
    isTrackLike,
    isFollowing,
    isFollower,
    addArrayValueByType,
    removeArrayValueByType,
    setUserData,updateIsLoginBlocked,
    updatePictureUrl,
    updateUserName
  };
};
export default useMyUserInfo;