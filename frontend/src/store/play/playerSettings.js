import {createSlice} from '@reduxjs/toolkit';
import {AUTO_PLAY} from "utill/enum/PlaybackTypes";
import {loadFromLocalStorage, saveToLocalStorage} from "../../utill/function";

const settings = {
  volume: 0.8,
  muted: false,
  played: 0,
  playedSeconds: 0,
  loaded: 0, //
  duration: 0, // 지속시간
  playbackRate: 1.0,
  playBackType: AUTO_PLAY,// 순차 재생 다음곡으로 재생
  startT: null
};

const initialState = {
  key: "local:playerSettings",
  item: settings
}
const playerSettings = createSlice({
  name: "playerSettings",
  initialState: initialState,
  reducers: {
    create(state) {
      const localStorage = loadFromLocalStorage(state.key);
      if (localStorage) {
        state.item.volume = localStorage.volume;
        state.item.playBackType = localStorage.playBackType;
        state.item.muted = localStorage.muted;
        return;
      }
      saveToLocalStorage(
          {
            key: state.key, item: {
              volume: state.item.volume,
              playBackType: state.item.playBackType,
              muted: state.item.muted,
            }
          }
      )
    }, updateSettings(state, action) {
      const key = action.payload.key;
      state.item[key] = action.payload.value;
      if (key === "muted" || key === "volume" || key === "playBackType") {
        const localStorage = loadFromLocalStorage(state.key);
        localStorage[key] = action.payload.value;
        saveToLocalStorage({key: state.key,item:localStorage});
      }
    }
  }
});

export let settingsActions = {
  create: playerSettings.actions.create,
  updateSettings: playerSettings.actions.updateSettings,
};
export default playerSettings.reducer;