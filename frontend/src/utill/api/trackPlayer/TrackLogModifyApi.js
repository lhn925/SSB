import {authTrackApi} from "utill/api/interceptor/ApiAuthInterceptor";
import {TRACKS_LOG,} from "utill/api/ApiEndpoints";

export default async function TrackLogModifyApi(body) {
   return authTrackApi.put(TRACKS_LOG, body);

}