import {authTrackApi} from "utill/api/interceptor/ApiAuthInterceptor";
import {
  TRACKS_INFO_SEARCH_LIST
} from "utill/api/ApiEndpoints";

export default async function TrackInfoSearchListApi(ids) {
  return await authTrackApi.get(TRACKS_INFO_SEARCH_LIST,{
    params: {
      ids:ids
    }
  });
}