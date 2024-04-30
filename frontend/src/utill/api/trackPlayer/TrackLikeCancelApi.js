import {authTrackApi} from "utill/api/interceptor/ApiAuthInterceptor";
import {TRACKS_LIKES} from "utill/api/ApiEndpoints";

export default async function TrackLikeCancelApi(id) {
  return await authTrackApi.delete(TRACKS_LIKES + id);
}