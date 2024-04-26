import TrackChartLogApi from "utill/api/trackPlayer/TrackChartLogApi";
import TrackLogModifyApi from "utill/api/trackPlayer/TrackLogModifyApi";


export function ChartLogSave(currentInfo, isChartLog, updateCurrPlayLog) {
  if (currentInfo.id === null || currentInfo.playLog === null) {
    return;
  }

  const miniNumPlayTime = currentInfo.playLog.miniNumPlayTime;
  const startTime = currentInfo.playLog.startTime;
  const playTime = Math.round(currentInfo.playLog.playTime);
  const trackId = currentInfo.id;
  const logToken = currentInfo.playLog.token;
  const isReflected = currentInfo.playLog.isReflected;
  updateCurrPlayLog("isChartLog", false);
  // 플레이 시간
  const closeTime = startTime + (playTime * 1000);
  // 최소시간 충족 여부 확인
  // console.log("zpzpp")

  if (miniNumPlayTime > playTime) {
    return;
  }
  // 조회수 반영 여부
  if (isReflected) {
    return;
  }
  const body = {
    token: logToken,
    playTime: playTime,
    closeTime: closeTime,
    isChartLog: isChartLog,
    trackInfoReqDto: {
      id: trackId
    }
  }
  updateCurrPlayLog("isReflected", true);
  handleChartTracking(isChartLog, body).catch((error) => {
    if (error.status === 422) { // 해당 경우에만 False
      // 나머지는 서버가 꺼져있거나 400 에러가 뜬다면 해당플레이를 집계하지 않음
      updateCurrPlayLog("isReflected", false);
    }
  })
}
async function handleChartTracking(isChartLog, body) {
  if (isChartLog) {
    return TrackChartLogApi(body);
  } else {
    return TrackLogModifyApi(body);
  }
}