import {createSlice} from "@reduxjs/toolkit";

const createTrackInfo = (data) => ({
  id: data.id,
  token: data.token,
  title: {value: data.title, message: '', error: false},
  desc: {value: '', message: '', error: false},
  isDownload: false,
  genreType: null,
  genre: null,
  customGenre: {value: '', message: '', error: false},
  isPlayList: data.isPlayList,
  isPrivacy: data.isPrivacy,
  tagList: [],
  order: null,
  uploadPercent: 0,
  coverImgFile: null,
  isSuccess:false
});

const initialState = {
  isPlayList: true,
  tracks: [],
  isSuccess:false,
  uploadPercent: 0,
  playList: {
    title: {value: '', message: '', error: false},
    desc: {value: '', message: '', error: false},
    playListType: null,
    genre: null,
    genreType: null,
    customGenre: {value: '', message: '', error: false},
    isPrivacy: false,
    isDownload: false,
    tagList: [],
    coverImgFile: null,
    error: false,
    message: null
  }
}

const uploadInfo = createSlice({
  name: "uploadInfo",
  initialState: initialState,
  reducers: {
    addTracks(state, action) {
      action.payload.tracks.forEach((value) => {
        state["tracks"].push(createTrackInfo(value));
      })
    }, addTrackTagList(state, action) {
      state.tracks.forEach((track) => {
        if (track.token === action.payload.token) {
          track.tagList = action.payload.tags;
        }
      })
    }, addPlayListTagList(state, action) {
      state.playList.tagList = action.payload.tags;
    }, setUploadPercent(state, action) {
      state.tracks.forEach(track => {
        if (track.token === action.payload.token) {
          track.uploadPercent = action.payload.uploadPercent;
        }
      });
    },
    removeTrack(state, action) {
      const removeIndex = state.tracks.findIndex(
          (track) => track.token === action.payload.token);
      state.tracks.splice(removeIndex, 1);
    },
    updateTracksValue(state, action) {
      state.tracks.forEach(track => {
        if (track.token === action.payload.token) {
          track[action.payload.key] = action.payload.value;
        }
      });
    },updatePlayListValue(state, action) {
      state.playList[action.payload.key] = action.payload.value;
    },updateTrackObject(state, action) {
      const key = action.payload.key;
      const subKey = action.payload.subKey;
      const value = action.payload.value;
      const token = action.payload.token;
      state.tracks.forEach(track => {
        if (track.token === token) {
          track[key] = {...track[key], [subKey]:value}
        }
      })
    },updatePlayListObject(state, action) {
      const key = action.payload.key;
      const subKey = action.payload.subKey;
      const value = action.payload.value;
      state.playList[key] = {...state.playList[key], [subKey]:value}
    },changeIsPrivacy(state, action) {
      const isPrivacy = action.payload.isPrivacy;
      state.tracks.forEach((tracks) => {
        tracks.isPrivacy = isPrivacy;
      })
      state.playList.isPrivacy = isPrivacy;
    }, changeIsPlayList(state, action) {
      const isPlayList = action.payload.isPlayList;
      state.tracks.forEach(tracks => {
        tracks.isPlayList = isPlayList;
      })
      state.isPlayList = isPlayList;
    }
  }
});

export let uploadInfoActions = {
  changeIsPrivacy: uploadInfo.actions.changeIsPrivacy,
  changeIsPlayList: uploadInfo.actions.changeIsPlayList,
  addTracks: uploadInfo.actions.addTracks,
  updateTracksValue: uploadInfo.actions.updateTracksValue,
  updatePlayListValue: uploadInfo.actions.updatePlayListValue,
  setUploadPercent: uploadInfo.actions.setUploadPercent,
  removeTrack: uploadInfo.actions.removeTrack,
  addPlayListTagList: uploadInfo.actions.addPlayListTagList,
  addTrackTagList: uploadInfo.actions.addTrackTagList,
  updateTrackObject: uploadInfo.actions.updateTrackObject,
  updatePlayListObject: uploadInfo.actions.updatePlayListObject
};
export default uploadInfo.reducer;