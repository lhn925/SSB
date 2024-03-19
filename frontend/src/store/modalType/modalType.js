import {createSlice} from "@reduxjs/toolkit";

const initialState = {
  type: null,
  visible: false
}
const modalType = createSlice({
  name: "modalType",
  initialState: initialState,
  reducers: {
    changeType(state, action) {
      state.type = action.payload.type;
    },
    closeModal(state) {
      state.visible = false;
    },
    openModal(state) {
      state.visible = true;
    }
  }
});

export let modalActions = {
  changeType: modalType.actions.changeType,
  closeModal: modalType.actions.closeModal,
  openModal: modalType.actions.openModal,
};
export default modalType.reducer;