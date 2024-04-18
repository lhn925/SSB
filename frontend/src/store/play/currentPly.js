import {createSlice} from '@reduxjs/toolkit';
import {
  loadFromLocalStorage,
  removeFromLocalStorage,
  saveToLocalStorage
} from "utill/function";
import {toast} from "react-toastify";

//현재 재생 목록

const createTrackInfo = (data) => ({
  index:data.index, // 순번
  id:data.id,
  title:data.title,
  userName:data.userName,
  coverUrl:data.coverUrl,
  trackLength:Number.parseInt(data.trackLength),
  isOwner:data.isOwner,
  isLike:data.isLike,
  isPrivacy:data.isPrivacy,
  createdDateTime:data.createdDateTime, // 재생목록에 추가한 날짜
})

const initialState = {
  key: "local:currentPly",
  item:[]
}

const currentPly = createSlice({
  name: "currentPly",
  initialState: initialState,
  reducers: {
    addTracks(state, action) {
      // 500개 제한
      if (state.item.length === 500) {
        toast.error("텍스트 처리) 오백개 제한")
        return;
      }



      loadFromLocalStorage()
      // const trackInfo = createTrackInfo(action.payload.data);

    }
  }
});

export let currentPlyActions = {
  addTracks: currentPly.actions.addTracks,
};
export default currentPly.reducer;