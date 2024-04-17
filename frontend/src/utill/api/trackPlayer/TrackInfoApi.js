import {authTrackApi} from "utill/api/interceptor/ApiAuthInterceptor";
import {TRACKS_INFO_SEARCH_ID} from "utill/api/ApiEndpoints";

export default async function TrackInfoApi(id) {
  return await authTrackApi(TRACKS_INFO_SEARCH_ID + id);
}