import {authTrackApi} from "utill/api/interceptor/ApiAuthInterceptor";
import {TRACKS_LOG_ID_CHART} from "utill/api/ApiEndpoints";

export default async function TrackChartLogApi(body) {
  return await authTrackApi.post(TRACKS_LOG_ID_CHART, body);
}