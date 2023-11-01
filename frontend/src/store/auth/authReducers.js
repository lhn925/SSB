import { configureStore, createSlice } from '@reduxjs/toolkit';

const authReducers = createSlice({
  name:"auth",
  initialState:{access:null,refresh:null},
  reducers :{
    setToken(state,action) {
      state.access = action.payload.accessToken;
      state.refresh = action.payload.refreshToken;
    }
  }
});

export let setToken = authReducers.actions;
export default authReducers.reducer;