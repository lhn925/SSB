import {createSlice} from '@reduxjs/toolkit';
import {PURGE} from "redux-persist/es/constants";
import {RESET_ALL} from "../actions/Types";

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
      state.access = action.payload.accessToken;
    }, setRefresh(state, action) {
      state.refresh = action.payload.refreshToken;
    }, setAccessHeader(state) {
      state.accessHeader = {"Authorization": state.access};
    }, setRefreshHeader(state) {
      state.refreshHeader = {
        "RefreshAuth": state.refresh
      };
    },
  }, extraReducers(builder) {
    builder.addCase(PURGE, () => initialState);
    builder.addCase(RESET_ALL, () => initialState);
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