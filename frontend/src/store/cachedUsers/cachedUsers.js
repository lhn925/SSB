import {createSlice} from "@reduxjs/toolkit";
import {RESET_ALL} from "store/actions/Types";
import {getFutureTimeInMilliseconds, getMinutes} from "../../utill/function";

const createUsersInfo = (data) => (
    {
      id: data.id,
      userName: data.userName,
      followerCount: data.followerCount,
      followingCount: data.followingCount,
      pictureUrl: data.pictureUrl,
      lastFetched: data.lastFetched // fetch 시간대
    });
const initialState = {
  users: Array.from(new Map().entries()),
  staleTime: getMinutes(5),
  cacheTime: getMinutes(10)
}
const cachedUsers = createSlice({
  name: "cachedUsers",
  initialState: initialState,
  reducers: {
    addUsers(state, action) {
      const user = action.payload.user;
      const usersMap = new Map(state.users);
      usersMap.set(user.id, createUsersInfo(user));
      state.users = Array.from(usersMap.entries());
    }, removeUser (state,action) {
      const id = action.payload.id;
      const userMap = new Map(state.users);
      userMap.delete(id);
      state.users = Array.from(userMap.entries());
    }
  }, extraReducers(builder) {
    builder.addCase(RESET_ALL, () => initialState);
  }
})

export let cachedUsersActions = {
  addUsers: cachedUsers.actions.addUsers,
  removeUser: cachedUsers.actions.removeUser,
};
export default cachedUsers.reducer;
