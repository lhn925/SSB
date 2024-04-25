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
  playTime: 1, // playTime Seconds
  isReflected: false // 조회수 반영 여부 true 면 더이상 api 요청 x
})

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
      if (action.payload.info === undefined) {
        return;
      }
      // playLog 초기화
      if (state.playLog !== null) {
        state.playLog = null;
      }
      setTrackInfo(state, action.payload.info);
    }, createPlayLog(state, action) {
      setTrackInfo(state, action.payload.info);
      state.playLog = createPlayLog(action.payload.playLog);
    }, updatePlayLog(state, action) {
      if (state.playLog === null) {
        return;
      }
      const key = action.payload.key;
      state.playLog[key] = action.payload.value;
    }, updateTrackInfo(state, action) {
      const key = action.payload.key;
      state[key] = action.payload.value;
    }, changeTrackInfo(state, action) {
      const trackInfo = action.payload.info;
      if (state.id === parseInt(trackInfo.id)) {
        setTrackInfo(state, trackInfo);
      }
    }
  }
});

export let currentActions = {
  create: currentTrack.actions.create,
  createPlayLog: currentTrack.actions.createPlayLog,
  updatePlayLog: currentTrack.actions.updatePlayLog,
  updateTrackInfo: currentTrack.actions.updateTrackInfo,
  changeTrackInfo: currentTrack.actions.changeTrackInfo,
};
export default currentTrack.reducer;