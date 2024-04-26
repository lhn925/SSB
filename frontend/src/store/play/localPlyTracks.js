import {createSlice} from '@reduxjs/toolkit';
import {toast} from "react-toastify";
import {createTrackInfo} from "../../utill/function";

//현재 재생 목록
/**
 *
 * 현재재생목록을 유지하면서
 *
 *
 * @type {{id: number, tracks: *[]}}
 */
const initialState = {
  tracks:[], // 최대 5개까지 중복 x로
}

const localPlyTracks = createSlice({
  name: "localPlyTracks",
  initialState: initialState,
  reducers: {

    addTrackInfo(state,action) {
      const data = action.payload.data;
      const trackInfo = createTrackInfo(data);
      if (state.tracks.length > 0) {
        const updateTracks = state.tracks.filter(track => track.id !== trackInfo.id);
        updateTracks.push(trackInfo);
        state.tracks = updateTracks;
      } else {
        state.tracks.push(trackInfo);
      }
    }
  }
});


export let localPlyTracksActions = {
  addTrackInfo: localPlyTracks.actions.addTrackInfo
};
export default localPlyTracks.reducer;