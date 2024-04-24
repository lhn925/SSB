import {createSlice} from '@reduxjs/toolkit';
import {
  getRandomInt,
  loadFromLocalStorage,
  removeFromLocalStorage,
  saveToLocalStorage
} from "utill/function";
import {toast} from "react-toastify";
import {LOCAL_PLAYER_SETTINGS, LOCAL_PLY_KEY} from "utill/enum/localKeyEnum";
import {shuffle} from "lodash";

//현재 재생 목록

const createTrackInfo = (data) => ({
  index: data.index, // 순번
  id: data.id,
  title: data.title,
  userName: data.userName,
  coverUrl: data.coverUrl,
  trackLength: Number.parseInt(data.trackLength),
  isOwner: data.isOwner,
  isLike: data.isLike,
  isPrivacy: data.isPrivacy,
  createdDateTime: data.createdDateTime, // 재생목록에 추가한 날짜
})

const initialState = {
  key: LOCAL_PLY_KEY,
  userId: null,
  playOrders: [], // 재생 순서를 저장하는
  item: []
}

const localPly = createSlice({
  name: "localPly",
  initialState: initialState,
  reducers: {
    create(state, data) {
      const localPly = loadFromLocalStorage(state.key);
      const userId = data.payload.userId;
      state.userId = userId;

      if (localPly) {
        const userIdNotEq = userId !== localPly.userId;
        if (userIdNotEq) {
          removeFromLocalStorage(state.key);
          return;
        }
        state.item = localPly.list;
        const settings = loadFromLocalStorage(LOCAL_PLAYER_SETTINGS);

        const orderArray = [];
        for (let i = 0; i < localPly.list.length; i++) {
          orderArray.push(i);
        }

        if (settings.shuffle) {
          state.playOrders = shuffle(orderArray);
          return;
        }
        state.playOrders = orderArray;
      }

    }, addTracks(state, action) {
      // 갖고 오기
      const localPly = loadFromLocalStorage(state.key);
      const data = action.payload.data;
      state.userId = data.userId;

      // 로컬 스토리지에 있다면
      if (localPly) {
        // 유저아이디가 일치하지 않는 경우
        // data.index = localPly.list.length + 1;
        // 가장 큰 index 를 가짐

        let maxIndex;

        if (localPly.list.length > 0) {
          maxIndex = localPly.list.reduce(
              (max, item) => Math.max(max, item.index), localPly.list[0].index);
        } else {
          // 배열이 비어 있으면 적절한 기본값을 설정
          maxIndex = 0;
        }
        data.index = maxIndex + 1;

        const userIdNotEq = data.userId !== localPly.userId;
        if (userIdNotEq) {
          // 삭제
          removeFromLocalStorage(state.key);
          data.index = 1;

        }
        // 500개 제한
        if (state.item.length === 500 || localPly.list.length === 500) {
          toast.error("텍스트 처리) 오백개 제한")
          return;
        }
        if (state.item.length === 0 && !userIdNotEq) {
          state.item = localPly.list;
        }
        const trackInfo = createTrackInfo(data);
        state.item.push(trackInfo);
        const settings = loadFromLocalStorage(LOCAL_PLAYER_SETTINGS);
        // // 만약 랜덤 재생 중 이라면
        if (settings.shuffle && state.item.length > 1) {
          // 현재 재생중인 Index 보다 무조건 위로 가게끔 한다
          const index = getRandomInt(data.playIndex,
              state.item.length); // 중간에 추가할 index
          // 둘이 같다면 맨 마지막에 추가
          if (index === state.playOrders.length) {
            state.playOrders.push(index);
          } else {
            // 중간에 추가
            state.playOrders.splice(index, 0, state.playOrders.length);
          }
        } else {
          state.playOrders.push(state.item.length - 1);
        }
        saveToLocalStorage(
            {key: state.key, item: {list: state.item, userId: data.userId}})
        return;
      }
      data.index = state.item.length + 1;
      state.item.push(createTrackInfo(data));
      state.playOrders.push(state.item.length - 1);
      saveToLocalStorage(
          {key: state.key, item: {list: state.item, userId: data.userId}})
    }, shuffleOrders(state, action) {
      const isShuffle = action.payload.isShuffle;

      // 현재 재생하고있는 위치 인덱스
      const playIndex = action.payload.playIndex;

      // 현재 재생 하고 있는 위치에 트랙값을 가져온다
      const currentOrders = state.playOrders[playIndex];
      console.log(currentOrders);
      // 이전값
      if (isShuffle) {
        const prevOrders = state.playOrders;
        // 재생하고 인덱스 값 삭제 후
        prevOrders.splice(playIndex, 1);
        const shuffleArray = shuffle(prevOrders);
        // 첫번째에 값 추가
        shuffleArray.splice(0, 0, currentOrders);
        state.playOrders = shuffleArray;
        return;
      }
      state.playOrders.sort(function(a, b)  {
        if(a > b) return 1;
        if(a === b) return 0;
        if(a < b) return -1;
      });
    }
  }
});

export let localPlyActions = {
  addTracks: localPly.actions.addTracks,
  create: localPly.actions.create,
  shuffleOrders: localPly.actions.shuffleOrders,
};
export default localPly.reducer;