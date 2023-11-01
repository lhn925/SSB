import {configureStore, createSlice} from '@reduxjs/toolkit';
import {PURGE} from "redux-persist/es/constants";

const initialState = {
  access: null,
  refresh: null,
  accessHeader: {},
  refreshHeader: {}
}
const authReducers = createSlice({
  name: "auth",
  initialState: initialState,
  reducers: {
    reset(state) {
      Object.assign(state, initialState)
    },
    setAccess(state, action) {
      let accessToken = action.payload.accessToken;
      state.access = accessToken;
    }, setRefresh(state, action) {
      let refreshToken = action.payload.refreshToken;
      state.refresh = refreshToken;
    }, setAccessHeader(state) {
      state.accessHeader = {"Authorization": state.access};
    }, setRefreshHeader(state) {
      state.refreshHeader = {
        "RefreshAuth": state.refresh
      };
    },
    extraReducers(builder) {
      builder.addCase(PURGE, () => initialState);
    }
  }
});

export let authActions = {
  reset: authReducers.actions.reset,
  setAccess: authReducers.actions.setAccess,
  setRefresh: authReducers.actions.setRefresh,
  setAccessHeader: authReducers.actions.setAccessHeader,
  setRefreshHeader: authReducers.actions.setRefreshHeader,
};
export default authReducers.reducer;