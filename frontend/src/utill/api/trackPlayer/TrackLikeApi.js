import {authApi, authTrackApi} from "utill/api/interceptor/ApiAuthInterceptor";
import {TRACKS_LIKES} from "utill/api/ApiEndpoints";

export default async function TrackLikeApi(id) {
  return await authTrackApi.post(TRACKS_LIKES + id);
}