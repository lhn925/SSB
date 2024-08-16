import {createSlice} from '@reduxjs/toolkit';
import {RESET_ALL} from "store/actions/Types";
import {getMinutes} from "../../utill/function";

export function createTrackInfo(data) {
  return {
    id: data.trackInfo.id,
    title: data.trackInfo.title,
    // userName: data.trackInfo.userName,
    coverUrl: data.trackInfo.coverUrl,
    trackLength: Number.parseInt(data.trackInfo.trackLength),
    isOwner: data.trackInfo.isOwner,
    isLike: data.trackInfo.isLike,
    isPrivacy: data.trackInfo.isPrivacy,
    postUser: {
      id: data.trackInfo.postUser.id,
      userName: data.trackInfo.postUser.userName
    },
    playCount:data.playCount,
    likeCount:data.likeCount,
    repostCount:data.repostCount,
    replyCount:data.replyCount,
    lastFetched: data.lastFetched, // fetch 시간대
    createdDateTime: data.createdDateTime, // 재생목록에 추가한 날짜
  }
}

/**
 *
 * 트랙정보 cached
 *
 * @type {{isVisible: boolean, tracks: *[]}}
 */
const initialState = {
  tracks: [],
  isVisible: false,
  staleTime: getMinutes(5),
  cacheTime: getMinutes(10)

}

const cachedTracks = createSlice({
  name: "cachedTracks",
  initialState: initialState,
  reducers: {
    addTrackInfo(state, action) {
      const data = action.payload.data;
      const trackInfo = createTrackInfo(data);
      if (state.tracks.length > 0) {
        const updateTracks = state.tracks.filter(
            track => track.id !== trackInfo.id);
        updateTracks.push(trackInfo);
        state.tracks = updateTracks;
      } else {
        state.tracks.push(trackInfo);
      }
    }, updatePlyTrackInfo(state, action) {
      const id = parseInt(action.payload.id);
      const key = action.payload.key;
      state.tracks.map((data) => {
        if (data.id === id) {
          data[key] = action.payload.value;
        }
      })
    },removeTrack(state,action) {
      const removeId = action.payload.id;
      state.tracks = state.tracks.filter(track => track.id !== removeId);
    }, changePlyVisible(state, action) {
      state.isVisible = action.payload.isVisible;
    }
  }, extraReducers: (builder) => {
    builder.addCase(RESET_ALL, () => initialState);
  }
});




export let cachedTracksActions = {
  addTrackInfo: cachedTracks.actions.addTrackInfo,
  updatePlyTrackInfo: cachedTracks.actions.updatePlyTrackInfo,
  changePlyVisible: cachedTracks.actions.changePlyVisible,
  removeTrack: cachedTracks.actions.removeTrack
};
export default cachedTracks.reducer;