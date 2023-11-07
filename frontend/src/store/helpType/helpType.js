import {createSlice} from "@reduxjs/toolkit";

const initialState = {
  helpType: null
}
const helpType = createSlice({
  name: "helpType",
  initialState: initialState,
  reducers: {
    changeType(state, action) {
      state.helpType = action.payload.helpType;
    }
  }
});

export let helpActions = {
  changeType: helpType.actions.changeType,
};
export default helpType.reducer;