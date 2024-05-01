import {createSlice} from '@reduxjs/toolkit';
import {removeFromLocalStorage, saveToLocalStorage} from "utill/function";
import {LOCAL_INST_} from "utill/enum/localKeyEnum";
import {RESET_ALL} from "../actions/Types";
const initialState = {
  key: null,
  item:{playing:false}
}
const playingReducer = createSlice({
  name: "playing",
  initialState: initialState,
  reducers: {
    changePlaying(state, action) {
      if (state.key === null) {
        state.key = LOCAL_INST_+ new Date().getTime();
      }
      state.item.playing = action.payload.isPlaying;
      saveToLocalStorage(state);
    },clear(state) {
      if (state.key !== null) {
        removeFromLocalStorage(state.key);
      }
    }
  },extraReducers: (builder) => {
    builder.addCase(RESET_ALL, () => initialState);
  }
});

export let playingActions = {
  changePlaying: playingReducer.actions.changePlaying,
  clear: playingReducer.actions.clear,
};
export default playingReducer.reducer;