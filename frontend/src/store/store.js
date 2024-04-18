import {combineReducers, configureStore} from '@reduxjs/toolkit';
import authReducer from "store/auth/authReducers"
import userReducer from "store/userInfo/userReducers"
import storage from "redux-persist/lib/storage"
import {persistReducer, persistStore} from "redux-persist";
import {
  FLUSH,
  PAUSE,
  PERSIST,
  PURGE, REGISTER,
  REHYDRATE
} from "redux-persist/es/constants";
import modalType from "store/modalType/modalType";
import helpType from "store/helpType/helpType";
import uploadInfo from "store/upload/uploadInfo";
import playingReducer from "store/play/playingReducer";
import currentTrack from "store/play/currentTrack";
import playerSettings from "store/play/playerSettings";
import localPly from "store/play/localPly";
const persistConfig = {
  key: 'root',//reducer의 어느 지점에서부터 데이터를 저장할 것 인지,
  version: 1,
  storage,
  whitelist:["authReducer","userReducer"]
};

const rootReducer = combineReducers({
  authReducer,
  userReducer,
  modalType,
  helpType,
  uploadInfo,
  playingReducer,
  currentTrack,
  playerSettings,
  localPly
})


const persistedReducer = persistReducer(persistConfig, rootReducer)
export const store = configureStore({
  reducer: persistedReducer,
  middleware: (getDefaultMiddleware) =>
      getDefaultMiddleware({
        serializableCheck: {
          ignoredActions: [FLUSH, REHYDRATE, PAUSE, PERSIST, PURGE, REGISTER],
        },
      }),
}
)

export const persistor = persistStore(store);
export default store;