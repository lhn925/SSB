import {createSlice} from '@reduxjs/toolkit';
import {toast} from "react-toastify";

//현재 재생 목록

const createTrackInfo = (data) => ({
  index: data.index, // 순번
  id: data.id,
  title: data.title,
  userName: data.userName,
  coverUrl: data.coverUrl,
  trackLength: Number.parseInt(data.trackLength),
  isOwner: data.isOwner,
  isLike: data.isLike,
  isPrivacy: data.isPrivacy,
  createdDateTime: data.createdDateTime, // 재생목록에 추가한 날짜
})

const createPlayInfo = (data) => ({
  id:data.id,
  title:data.title,
  coverUrl: data.coverUrl,
  tracks:[]
});

/**
 *
 * 현재재생목록을 유지하면서
 *
 *
 * @type {{id: number, tracks: *[]}}
 */
const initialState = {
  playList:[], // 최대 5개까지 중복 x로
}

const currentPly = createSlice({
  name: "currentPly",
  initialState: initialState,
  reducers: {
    addPlayList(state,action) {
      if (state.tracks.length > 5) {
        toast.error("텍스트 추가) 최대 5개까지 가능합니다.");
        return;
      }
      if (state.tracks.length > 0) {


      }


      // 로컬 재생 목록 일 경우
      const playInfo = createPlayInfo(action.payload.data);
      playInfo.tracks = action.payload.tracks.map(track => createTrackInfo(track));
    }
  }
});

export let localPlyActions = {
  addTracks: currentPly.actions.addTracks,
  create: currentPly.actions.create,
};
export default currentPly.reducer;