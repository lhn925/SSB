import {createSlice} from '@reduxjs/toolkit';
import {
  createPlyInfo,
  getRandomInt,
  loadFromLocalStorage,
  removeFromLocalStorage,
  saveToLocalStorage,
  sorted
} from "utill/function";
import {toast} from "react-toastify";
import {LOCAL_PLAYER_SETTINGS, LOCAL_PLY_KEY} from "utill/enum/localKeyEnum";
import {shuffle} from "lodash";
import {LOCAL_PLY_TRACK_RESET, RESET_ALL} from "store/actions/Types";

//현재 재생 목록
const initialState = {
  key: LOCAL_PLY_KEY,
  userId: null,
  playOrders: [], // 재생 순서를 저장하는
  item: []
}

function setStoragePly(state, userId) {
  state.item.sort(function (a, b) {
    return sorted(a, b);
  });
  saveToLocalStorage({key: state.key, item: {list: state.item, userId: userId}})
}



const localPly = createSlice({
  name: "localPly",
  initialState: initialState,
  reducers: {
    create(state, action) {
      const localPly = action.payload.localPly;
      const userId = action.payload.userId;
      state.userId = userId;
      if (localPly) {
        const userIdNotEq = userId !== localPly.userId;
        if (userIdNotEq) {
          removeFromLocalStorage(state.key);
          return;
        }

        localPly.list.sort(function (a, b) {
          return sorted(a, b);
        });

        const settings = loadFromLocalStorage(LOCAL_PLAYER_SETTINGS);
        const orderArray = [];
        for (let i = 0; i < localPly.list.length; i++) {
          orderArray.push(i);
          localPly.list[i].index = i + 1;
        }
        state.item = localPly.list;
        if (settings.shuffle) {
          state.playOrders = shuffle(orderArray);
          return;
        }
        state.playOrders = orderArray;
        setStoragePly(state, userId);
        return;
      }
      state.item = [];
      setStoragePly(state, userId);
    }, addTracks(state, action) {
      // 갖고 오기
      const localPly = loadFromLocalStorage(state.key);
      const data = action.payload.data;
      const limitText = action.payload.text;
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
        const statusOnLocalPly = state.item.filter((data) => data.isStatus === 1);
        const localPlyStatusList = localPly.list.filter((data) => data.isStatus === 1);

        const limit = 500;
        // 500개 제한
        if (statusOnLocalPly.length > limit || localPlyStatusList.length > limit) {
          toast.error(limitText)
          return;
        }
        if (state.item.length === 0 && !userIdNotEq) {
          state.item = localPly.list;
        }
        const trackInfo = createPlyInfo(data);
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
        setStoragePly(state, data.userId);
        return;
      }
      data.index = state.item.length + 1;
      state.item.push(createPlyInfo(data));
      state.playOrders.push(state.item.length - 1);
      saveToLocalStorage(
          {key: state.key, item: {list: state.item, userId: data.userId}})
    }, shuffleOrders(state, action) {
      state.playOrders = action.payload.playOrders;
    }, updatePlyTrackInfo(state, action) {
      const id = parseInt(action.payload.id);
      const key = action.payload.key;
      state.item.map((data) => {
        if (data.id === id) {
          data[key] = action.payload.value;
        }
      })
      // shuffleOrders 구조 변경 해야함
      setStoragePly(state, state.userId);
    }, changePlyTrackInfo(state, action) {
      const data = action.payload.data;
      const id = parseInt(data.id)
      state.item.map((track) => {
        const index = track.index;
        if (track.id === id) {
          track = createPlyInfo(data);
          track.index = index;
        }
      })
      setStoragePly(state, state.userId);
    }, removePlyByTrackId(state, action) {
      // play
      const removeId = action.payload.id;
      // const prevList = state.item;
      //
      // const findRemoveList = prevList.filter(track => track.id === removeId);
      //
      // if (findRemoveList.length > 0) {
      //   const minIndex = findRemoveList.reduce((max, item) => Math.min(max, item.index),
      //       findRemoveList[0].index);
      //   const updateList = prevList.filter(track => track.id !== removeId);
        state.item.map(data => {
          if (data.id === removeId) {
            data.isStatus = 0;
          }
        })
        // for (let i = minIndex - 1; i < updateList.length; i++) {
        //   updateList[i].index = i + 1; // 인덱스 재조정
        // }
        // state.item = updateList;
        setStoragePly(state, state.userId);
    }, removePlyByIndex(state, action) {
      // const updateList = action.payload.updateList;
      // if (updateList) {
      //   state.item = updateList;
      //   setStoragePly(state, state.userId);
      // }
      // const copyItem = [...state.item];
      state.item.map(data => {
        if (data.index === action.payload.index) {
          data.isStatus = 0;
        }
      })
      setStoragePly(state, state.userId);

    }, changeOrder(state, action) {
      // 바뀐 위치아래는 index + 1;
      state.item = action.payload.items;
      setStoragePly(state, state.userId);
    }
  }, extraReducers: (builder) => {
    builder.addCase(RESET_ALL, () => initialState);
    builder.addCase(LOCAL_PLY_TRACK_RESET, () => initialState);
  }
});

export let localPlyActions = {
  addTracks: localPly.actions.addTracks,
  create: localPly.actions.create,
  shuffleOrders: localPly.actions.shuffleOrders,
  // updatePlyTrackInfo: localPly.actions.updatePlyTrackInfo,
  changePlyTrackInfo: localPly.actions.changePlyTrackInfo,
  removePlyByTrackId: localPly.actions.removePlyByTrackId,
  changeOrder: localPly.actions.changeOrder,
  removePlyByIndex: localPly.actions.removePlyByIndex,
};
export default localPly.reducer;