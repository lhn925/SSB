import {createSlice} from "@reduxjs/toolkit";

const createTrackInfo = (data) => ({
  id: data.id,
  token: data.token,
  title: data.title,
  desc: null,
  isDownload: false,
  genreType: null,
  genre: null,
  isPlayList: data.isPlayList,
  isPrivacy: data.isPrivacy,
  tagList: [],
  order: null,
  uploadPercent: null,
  coverImgFile: null,
});

const initialState = {
  isPlayList: true,
  isPrivacy: false,
  tracks: [],
  playList: {
    title: null,
    desc: null,
    playListType: null,
    genre: null,
    isPlayList: false,
    isPrivacy: false,
    isDownload: false,
    uploadPercent: null,
    tagList: [],
    coverImgFile: null,
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
    }, updatePlayListValue(state, action) {
      state.playList[action.payload.key] = action.payload.value;
    }, changeIsPrivacy(state, action) {
      const isPrivacy = action.payload.isPrivacy;
      state.tracks.forEach((tracks) => {
        tracks.isPrivacy = isPrivacy;
      })
      state.isPrivacy = isPrivacy;
    }

    , changeIsPlayList(state, action) {
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
  setUploadPercent: uploadInfo.actions.setUploadPercent,
  removeTrack: uploadInfo.actions.removeTrack
};
export default uploadInfo.reducer;