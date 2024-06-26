import {createSlice} from '@reduxjs/toolkit';
import {PURGE} from "redux-persist/es/constants";
import {RESET_ALL} from "store/actions/Types";

const initialState = {
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
    }, addTrackLikedId(state, action) {
      state.trackLikedIds.concat(action.payload.trackLikedIds);

      console.log(state.trackLikedIds);
    }
  }, extraReducers(builder) {
    builder.addCase(PURGE, () => initialState);
    builder.addCase(RESET_ALL, () => initialState);
  }
});

export let userActions = {
  rest: userReducers.actions.reset,
  setUserId: userReducers.actions.setUserId,
  setEmail: userReducers.actions.setEmail,
  setPictureUrl: userReducers.actions.setPictureUrl,
  setUserName: userReducers.actions.setUserName,
  setIsLoginBlocked: userReducers.actions.setIsLoginBlocked,
  addTrackLikedId: userReducers.actions.addTrackLikedId,
};

export default userReducers.reducer;