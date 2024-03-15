import {configureStore, createSlice} from '@reduxjs/toolkit';
import {PURGE} from "redux-persist/es/constants";

const initialState = {
  userId: null,
  email: null,
  pictureUrl: null,
  userName: null,
  isLoginBlocked:null,
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
    }
  },
  extraReducers(builder) {
    builder.addCase(PURGE, () => initialState);
  }
});

export let userActions = {
  reset: userReducers.actions.reset,
  setUserId: userReducers.actions.setUserId,
  setEmail: userReducers.actions.setEmail,
  setPictureUrl: userReducers.actions.setPictureUrl,
  setUserName: userReducers.actions.setUserName,
  setIsLoginBlocked: userReducers.actions.setIsLoginBlocked,
};

export default userReducers.reducer;