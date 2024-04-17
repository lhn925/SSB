/**
 *
 *
 * 현재 재생 할려고 하는 track
 */
import {createSlice} from '@reduxjs/toolkit';

const createPlayLog = (data) => ({
  token: data.token,
  startTime: data.startTime,
  miniNumPlayTime: data.miniNumPlayTime,
  isChartLog: data.isChartLog,
})

/*
const createTrackInfo = (data) => ({
  id: data.id,
  title: data.title,
  userName: data.userName,
  trackLength: data.trackLength,
  coverUrl: data.coverUrl,
  isOwner: data.isOwner,
  isLike: data.isLike,
  isPrivacy: data.isPrivacy,
  createdDateTime: data.createdDateTime,
  playLog: null,
});
*/

const setTrackInfo = (info, data) => {
  info.id = Number.parseInt(data.id)
  info.title = data.title
  info.userName = data.userName
  info.trackLength = Number.parseInt(data.trackLength);
  info.coverUrl = data.coverUrl
  info.isOwner = data.isOwner
  info.isLike = data.isLike
  info.isPrivacy = data.isPrivacy
  info.createdDateTime = data.createdDateTime
};

const initialState = {
  id: -1,
  title: null,
  userName: null,
  trackLength: 0,
  coverUrl: null,
  isOwner: false,
  isLike: false,
  isPrivacy: false,
  createdDateTime: null,
  playLog: null,
}
const currentTrack = createSlice({
  name: "currentTrack",
  initialState: initialState,
  reducers: {
    create(state, action) {
      // 현재 저장되어 있는 id 가 같다면
      setTrackInfo(state, action.payload.info);
      if (action.payload.playLog !== undefined) {
        state.playLog =createPlayLog(action.payload.playLog);
      }
    }, updatePlayLog(state, action) {
      if (state.id === null) {
        return;
      }
      state.playLog = createPlayLog(action.payload.playLog);
    }
  }
});

export let currentActions = {
  create: currentTrack.actions.create,
  updatePlayLog: currentTrack.actions.updatePlayLog,
};
export default currentTrack.reducer;