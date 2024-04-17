import {authTrackApi} from "utill/api/interceptor/ApiAuthInterceptor";
import {TRACKS_LOG_ID} from "utill/api/ApiEndpoints";

export default async function TrackPlayApi(id) {
  return await authTrackApi.post(TRACKS_LOG_ID + id);
}