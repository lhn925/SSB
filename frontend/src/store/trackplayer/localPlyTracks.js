import {createSlice} from '@reduxjs/toolkit';
import {toast} from "react-toastify";
import {createTrackInfo, saveToLocalStorage} from "utill/function";
import {RESET_ALL} from "store/actions/Types";
import {LOCAL_INST_} from "utill/enum/localKeyEnum";

//현재 재생 목록
/**
 *
 * 현재재생목록을 유지하면서
 *
 *
 * @type {{id: number, tracks: *[]}}
 */
const initialState = {
  tracks: [], // 최대 5개까지 중복 x로
  isVisible: false // ply 열림 여부
}

const localPlyTracks = createSlice({
  name: "localPlyTracks",
  initialState: initialState,
  reducers: {
    addTrackInfo(state, action) {
      const data = action.payload.data;
      const trackInfo = createTrackInfo(data);
      if (state.tracks.length > 0) {
        const updateTracks = state.tracks.filter(
            track => track.id !== trackInfo.id);
        updateTracks.push(trackInfo);
        state.tracks = updateTracks;
      } else {
        state.tracks.push(trackInfo);
      }
    }, updatePlyTrackInfo(state, action) {
      const id = parseInt(action.payload.id);
      const key = action.payload.key;
      state.tracks.map((data) => {
        if (data.id === id) {
          data[key] = action.payload.value;
        }
      })
    }, changePlyVisible(state, action) {
      state.isVisible = action.payload.isVisible;
    }
  }, extraReducers: (builder) => {
    builder.addCase(RESET_ALL, () => initialState);
  }
});

export let localPlyTracksActions = {
  addTrackInfo: localPlyTracks.actions.addTrackInfo,
  updatePlyTrackInfo: localPlyTracks.actions.updatePlyTrackInfo,
  changePlyVisible: localPlyTracks.actions.changePlyVisible
};
export default localPlyTracks.reducer;