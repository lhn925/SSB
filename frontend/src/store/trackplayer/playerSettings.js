import {createSlice} from '@reduxjs/toolkit';
import {loadFromLocalStorage, saveToLocalStorage} from "utill/function";
import {LOCAL_PLAYER_SETTINGS} from "utill/enum/localKeyEnum";
import {RESET_ALL} from "store/actions/Types";

const settings = {
  order: 0, // 재생위치정보
  volume: 0.5,
  muted: false,
  played: 0,
  playedSeconds: 0,
  loaded: 0, //
  duration: 0, // 지속시간
  playbackRate: 1.0,
  playBackType: 0,// 순차 재생 다음곡으로 재생
  shuffle: false,// 랜덤재생
  startT: null
};
const initialState = {
  key: LOCAL_PLAYER_SETTINGS,
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
        state.item.shuffle = localStorage.shuffle;
        state.item.muted = localStorage.muted;
        return;
      }
      saveToLocalStorage(
          {
            key: state.key, item: {
              volume: state.item.volume,
              playBackType: state.item.playBackType,
              muted: state.item.muted,
              shuffle: state.item.shuffle,
            }
          }
      )
    }, updateSettings(state, action) {
      const key = action.payload.key;
      state.item[key] = action.payload.value;
      if (key === "muted" || key === "volume" || key === "playBackType" || key === "order" || key === "shuffle") {
        const localStorage = loadFromLocalStorage(state.key);
        localStorage[key] = action.payload.value;
        saveToLocalStorage({key: state.key, item: localStorage});
      }
    }
  },extraReducers: (builder) => {
    builder.addCase(RESET_ALL, () => initialState);
  }
});

export let settingsActions = {
  create: playerSettings.actions.create,
  updateSettings: playerSettings.actions.updateSettings,
};
export default playerSettings.reducer;