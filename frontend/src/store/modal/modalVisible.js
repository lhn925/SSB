import {createSlice} from "@reduxjs/toolkit";

const initialState = {
  modalVisible: false
}
const modalVisible = createSlice({
  name: "modalVisible",
  initialState: initialState,
  reducers: {
    closeModal(state) {
      state.modalVisible = false;
    },
    openModal(state) {
      state.modalVisible = true;
    }
  }
});

export let modalVisibleActions = {
  closeModal: modalVisible.actions.closeModal,
  openModal: modalVisible.actions.openModal,
};
export default modalVisible.reducer;