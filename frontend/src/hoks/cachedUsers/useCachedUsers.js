import {useDispatch, useSelector} from "react-redux";
import {cachedUsersActions} from "store/cachedUsers/cachedUsers";
import FetchUsersProfileApi from "utill/api/users/FetchUsersProfileApi";
import {toast} from "react-toastify";

const useCachedUsers = () => {
  /**
   * cacheTime: 캐시된 데이터를 메모리에 얼마나 오래 유지할지 결정.
   * staleTime: 데이터가 신선한 상태로 간주되는 시간.
   */
  const cachedUsers = useSelector((state) => state?.cachedUsers);
  const dispatch = useDispatch();

  const addUsers = (user) => {
    if (user == null) {
      return;
    }
    dispatch(cachedUsersActions.addUsers({user: user}));
  }

  const removeUser = (id) => {
    dispatch(cachedUsersActions.removeUser({id: id}));
  }
  const fetchUserByUserName = (userName) => {
    // 캐시에 사용자가 없는 경우 null 반환
    if (cachedUsers.users.length === 0) {
      return null;
    }
    const userMap = new Map(cachedUsers.users);
    const userArray = userMap.values();
    // 캐시에서 사용자 검색

    for (const user of userArray) {
      // 사용자가 발견된 경우 해당 사용자 반환, 그렇지 않으면 null 반환
      if (user.userName === userName) {
        return fetchUsers(user.id);
      }
    }
    return null;
  };

  const fetchUsers = async (...ids) => {
    if (ids == null || ids.length === 0) {
      return;
    }
    const staleTime = cachedUsers.staleTime;
    const cacheTime = cachedUsers.cacheTime;

    const fetchUsers = [];
    const userMap = new Map(cachedUsers.users);
    const findIds = [];// 캐쉬에 없거나 cacheTime 이 지나버린 경우
    const now = Date.now();
    for (const id of ids) {
      const user = userMap.get(id);
      if (user == null) {
        findIds.push(id);
        continue;
      }
      const lastFetched = user.lastFetched;
      const isCacheExpired = now - lastFetched > cacheTime;
      const isStaleExpired = now - lastFetched > staleTime;

      // cacheTime 혹은 staleTime 만료 된 경우 다시 fetch 하기위해 redux 삭제 후 findIds에 리스트에 추가ㄴ
      if (isCacheExpired || isStaleExpired) {
        removeUser(id);
        findIds.push(id);
        // staleTime이 만료되지 않은 경우 list에 담아서 반환
      } else if (!isStaleExpired) {
        fetchUsers.push(user);
      }
    }

    if (findIds.length === 0) {
      return fetchUsers;
    }

    // 찾을려는 유저가 cache에 없거나 cacheTime이 만료된 경우
    if (findIds.length > 0) {
      const response = await FetchUsersProfileApi(findIds);
      if (response.code === 200 && response.data.length > 0) {
        const nowDate = Date.now();
        response.data.map(user => {
          user.lastFetched = nowDate;
          addUsers(user);
          fetchUsers.push(user);
        });
      }
      if (response.code === 400) {
        toast.error(response.data?.errorDetails[0].message);
      }
      return fetchUsers;
    }
  }

  return {
    addUsers, cachedUsers, fetchUsers, removeUser,fetchUserByUserName
  }
};
export default useCachedUsers;