import {createSlice} from '@reduxjs/toolkit';
import {PURGE} from "redux-persist/es/constants";
import {RESET_ALL} from "store/actions/Types";

export const TRACK_LIKED_IDS = "TRACK_LIKED_IDS";
export const FOLLOWING_IDS = "FOLLOWING_IDS";
export const FOLLOWER_IDS = "FOLLOWER_IDS";
const initialState = {
  id: 0,
  userId: null,
  email: null,
  pictureUrl: null,
  userName: null,
  isLoginBlocked: null,
  trackLikedIds: [],
  followingIds: [],
  followerIds: [],
  trackUploadCount: 0
}
const userReducers = createSlice({
  name: "user",
  initialState: initialState,
  reducers: {
    reset(state) {
      Object.assign(state, initialState)
    },
    setUid(state,action){
      state.id = action.payload.id;
    },
    setUserId(state, action) {
      state.userId = action.payload.userId;
    },
    setIsLoginBlocked(state, action) {
      state.isLoginBlocked = action.payload.isLoginBlocked;
    },
    setEmail(state, action) {
      state.email = action.payload.email;
    },
    setPictureUrl(state, action) {
      state.pictureUrl = action.payload.pictureUrl;
    },
    setUserName(state, action) {
      state.userName = action.payload.userName;
    }, setArrayByType(state, action) {
      switch (action.payload.type) {
        case TRACK_LIKED_IDS:
          state.trackLikedIds = action.payload.ids;
          break;
        case FOLLOWING_IDS:
          state.followingIds = action.payload.ids;
          break;
        case FOLLOWER_IDS:
          state.followerIds = action.payload.ids;
          break;
      }
    }, addArrayValueByType(state, action) {
      const id = action.payload.id;
      switch (action.payload.type) {
        case TRACK_LIKED_IDS:
          state.trackLikedIds.push(id);
          break;
        case FOLLOWING_IDS:
          state.followingIds.push(id);
          break;
        case FOLLOWER_IDS:
          state.followerIds.push(id);
          break;
      }
    }
  }, extraReducers(builder) {
    builder.addCase(PURGE, () => initialState);
    builder.addCase(RESET_ALL, () => initialState);
  }
});

export let userActions = {
  rest: userReducers.actions.reset,
  setUid: userReducers.actions.setUid,
  setUserId: userReducers.actions.setUserId,
  setEmail: userReducers.actions.setEmail,
  setPictureUrl: userReducers.actions.setPictureUrl,
  setUserName: userReducers.actions.setUserName,
  setIsLoginBlocked: userReducers.actions.setIsLoginBlocked,
  setArrayByType: userReducers.actions.setArrayByType,
};

export default userReducers.reducer;