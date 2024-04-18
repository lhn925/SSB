import {createSlice} from '@reduxjs/toolkit';
import {
  loadFromLocalStorage,
  removeFromLocalStorage,
  saveToLocalStorage
} from "utill/function";
import {toast} from "react-toastify";
import {LOCAL_PLY_KEY} from "utill/enum/localKeyEnum";

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
          maxIndex = localPly.list.reduce((max, item) => Math.max(max, item.index), localPly.list[0].index);
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

        state.item.push(createTrackInfo(data));
        saveToLocalStorage(
            {key: state.key, item: {list: state.item, userId: data.userId}})
        return;
      }

      data.index = state.item.length + 1;
      state.item.push(createTrackInfo(data));
      saveToLocalStorage(
          {key: state.key, item: {list: state.item, userId: data.userId}})
    }
  }
});

export let localPlyActions = {
  addTracks: localPly.actions.addTracks,
  create: localPly.actions.create,
};
export default localPly.reducer;