import {createSlice} from "@reduxjs/toolkit";
import {
  recalculateTotalUploadPercent
} from "utill/function";

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
  order: data.order,
  uploadPercent: 0,
  coverImgFile: null,
  isSuccess: false,
  // isSave: false // 사용자가 save 버튼을 눌렀을 경우 업데이트
});
const createPlayListInfo = {
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
  message: null,
  // isSave: false // 사용자가 save 버튼을 눌렀을 경우 업데이트
};

const createSaveInfo = (data) => ({
      id: data.id,
      token: data.token,
      userName:data.userName,
      title: data.title,
      desc: data.desc,
      coverUrl: data.coverUrl,
      tagList: data.tagList,
      isPlayList: data.isPlayList,
      isPrivacy: data.isPrivacy,
      tracks: data.isPlayList ? data.tracks : null
    }
)

const initialState = {
  isPlayList: true,
  tracks: [],
  isSuccess: false,
  uploadPercent: 0,
  playList: createPlayListInfo,
  saves: [] // save 한 트랙 리스트 혹은 플레이 리스트 정보
}
const uploadInfo = createSlice({
  name: "uploadInfo",
  initialState: initialState,
  reducers: {
    addTracks(state, action) {
      action.payload.tracks.forEach((value) => {
        if (state.tracks.length === 0) {
          state.uploadPercent = 0;
        }
        state.tracks.push(createTrackInfo(value));
      })
    }, addSaves(state, action) {
      const saveInfo = createSaveInfo(action.payload.data);
      state.saves.push(saveInfo);
    }, addTrackTagList(state, action) {
      state.tracks.forEach((track) => {
        if (track.token === action.payload.token) {
          track.tagList = action.payload.tags;
        }
      })
    }, addPlayListTagList(state, action) {
      state.playList.tagList = action.payload.tags;
    }, setTracksUploadPercent(state, action) {
      const findTracks = state.tracks.filter(
          track => track.token === action.payload.token);
      if (findTracks.length !== 0) {
        const track = findTracks[0];
        const trackUploadPercent = action.payload.uploadPercent;

        // 완료 됐으면 isSuccess true
        track.isSuccess = trackUploadPercent === 100;
        track.uploadPercent = trackUploadPercent;
        state.uploadPercent = recalculateTotalUploadPercent(state.tracks);
        state.isSuccess = state.tracks.every(
            track => track.uploadPercent === 100);

      } else {
        throw new Error();
      }
    }, cleanStore(state) {
      state.tracks = [];
      Object.assign(state.playList, createPlayListInfo);
    }, removeTrack(state, action) {
      if (action.payload.token === undefined) {
        return;
      }
      const totalFiles = state.tracks.length; // 원래의 전체 파일 수
      // 삭제 시 to
      if (totalFiles <= 1) {
        state.isSuccess = false;
        state.uploadPercent = 0;
      }
      state.tracks = state.tracks.filter(
          value => action.payload.token !== value.token);
      state.uploadPercent = recalculateTotalUploadPercent(state.tracks);
    }, updateOrder(state, action) {
      const items = Array.from(state.tracks);
      const sourceIndex = action.payload.sourceIndex;
      const destinationIndex = action.payload.destIndex;
      const [reorderedItem] = items.splice(sourceIndex, 1);
      items.splice(destinationIndex, 0, reorderedItem);
      state.tracks = items;
    },
    updateTracksValue(state, action) {
      state.tracks.forEach(track => {
        if (track.token === action.payload.token) {
          track[action.payload.key] = action.payload.value;
        }
      });
    }, updatePlayListValue(state, action) {
      state.playList[action.payload.key] = action.payload.value;
    }, updateTrackObject(state, action) {
      const key = action.payload.key;
      const subKey = action.payload.subKey;
      const value = action.payload.value;
      const token = action.payload.token;
      state.tracks.forEach(track => {
        if (track.token === token) {
          track[key] = {...track[key], [subKey]: value}
        }
      })
    }, updatePlayListObject(state, action) {
      const key = action.payload.key;
      const subKey = action.payload.subKey;
      const value = action.payload.value;
      state.playList[key] = {...state.playList[key], [subKey]: value}
    }, changeIsPrivacy(state, action) {
      const isPrivacy = action.payload.isPrivacy;
      state.tracks.forEach((track) => {
        track.isPrivacy = isPrivacy;
      })
      state.playList.isPrivacy = isPrivacy;
    }, changeIsPlayList(state, action) {
      const isPlayList = action.payload.isPlayList;
      state.tracks.forEach(track => {
        track.isPlayList = isPlayList;
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
  setTracksUploadPercent: uploadInfo.actions.setTracksUploadPercent,
  removeTrack: uploadInfo.actions.removeTrack,
  addPlayListTagList: uploadInfo.actions.addPlayListTagList,
  addTrackTagList: uploadInfo.actions.addTrackTagList,
  updateTrackObject: uploadInfo.actions.updateTrackObject,
  updatePlayListObject: uploadInfo.actions.updatePlayListObject,
  updateOrder: uploadInfo.actions.updateOrder,
  cleanStore: uploadInfo.actions.cleanStore,
  addSaves: uploadInfo.actions.addSaves
};
export default uploadInfo.reducer;