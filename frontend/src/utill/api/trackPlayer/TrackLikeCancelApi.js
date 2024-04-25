import {authApi} from "utill/api/interceptor/ApiAuthInterceptor";
import {TRACKS_LIKES} from "utill/api/ApiEndpoints";

export default async function TrackLikeCancelApi(id) {
  return authApi.delete(TRACKS_LIKES + id);
}