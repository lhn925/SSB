/**
 *
 *
 * 현재 재생 할려고 하는 track
 */
import {createSlice} from '@reduxjs/toolkit';
import {RESET_ALL} from "store/actions/Types";

const setPlayLog = (playLog, data) => {
  playLog.trackId = data.trackId
  playLog.token = data.token
  playLog.startTime = data.startTime
  playLog.miniNumPlayTime = data.miniNumPlayTime
  playLog.isChartLog = data.isChartLog
};

const setTrackInfo = (info, data) => {
  info.id = Number.parseInt(data.id)
  info.title = data.title
  info.postUser.userName = data.postUser.userName
  info.postUser.id = data.postUser.id
  info.postUser.isFollow = data.postUser.isFollow
  info.trackLength = Number.parseInt(data.trackLength);
  info.coverUrl = data.coverUrl
  info.isOwner = data.isOwner
  info.isLike = data.isLike
  info.isPrivacy = data.isPrivacy
  info.createdDateTime = data.createdDateTime
};

const playLog = {
  trackId: -1,
  token: null,
  startTime: null,
  miniNumPlayTime: 0,
  isChartLog: false,
  playTime: 1, // playTime Seconds
  isReflected: false
}


const trackInfo = {
  id: -1,
  title: null,
  trackLength: 0,
  coverUrl: null,
  isOwner: false,
  isLike: false,
  isPrivacy: false,
  postUser:{
    userName: null,
    id:-1,
    isFollow:false,
  },
  createdDateTime: null
}
const initialState = {
  info: trackInfo,
  playLog: playLog
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
      if (state.playLog.id !== -1) {
        state.playLog = playLog;
      }
      setTrackInfo(state.info, action.payload.info);
    }, createPlayLog(state, action) {
      setTrackInfo(state.info, action.payload.info);
      action.payload.playLog.trackId = action.payload.info.id;
      setPlayLog(state.playLog, action.payload.playLog);
    }, updatePlayLog(state, action) {
      const key = action.payload.key;
      state.playLog[key] = action.payload.value;
    }, updateTrackInfo(state, action) {
      const key = action.payload.key;
      state.info[key] = action.payload.value;
    }, changeTrackInfo(state, action) {
      const trackInfo = action.payload.info;
      if (trackInfo == null) {
        return;
      }
      if (state.info.id === parseInt(trackInfo.id)) {
        setTrackInfo(state.info, trackInfo);
      }
    }
  },extraReducers: (builder) => {
    builder.addCase(RESET_ALL, () => initialState);
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