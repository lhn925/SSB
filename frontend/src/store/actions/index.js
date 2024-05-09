import {
  CURRENT_TRACK_RESET,
  LOCAL_PLY_TRACK_RESET,
  RESET_ALL
} from "store/actions/Types";

export function resetAll() {
  return {type: RESET_ALL};
}

export function resetCurrentTrack() {
  return {type: CURRENT_TRACK_RESET};
}
export function resetLocalPlyTrack() {
  return {type: LOCAL_PLY_TRACK_RESET};
}