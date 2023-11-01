import {createSlice} from "@reduxjs/toolkit";

const initialState = {
  type: null,
  modalClose: null
}
const modalType = createSlice({
  name: "modalType",
  initialState: initialState,
  reducers: {
    changeType(state, action) {
      state.type = action.payload.type;
    }
  }
});

export let modalActions = {
  changeType: modalType.actions.changeType,
};
export default modalType.reducer;