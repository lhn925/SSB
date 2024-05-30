import React, {useEffect, useRef, useState} from 'react';
import 'css/playerBar/playerBar.css';
import 'css/track/track.css';
import ReactPlayer from "react-player";

import {USERS_FILE_IMAGE, USERS_FILE_TRACK_PLAY} from "utill/api/ApiEndpoints";
import {
  durationTime,
  loadFromLocalStorage, removeFromLocalStorage,
  secondsToTime, shufflePlayOrder,
} from "utill/function";
import {
  ALL_PLAY,
  playBackTypes,
  REPEAT_ALL,
  REPEAT_ONE
} from "utill/enum/PlaybackTypes";
import {toast} from "react-toastify";
import {ToggleLike} from "content/trackplayer/ToggleLike";
import {ChartLogSave} from "content/trackplayer/ChartLogSave";
import {ToggleFollow} from "content/trackplayer/ToggleFollow";
import {PlayList} from "components/trackplayer/PlayList";
import {MINUS, PLUS} from "content/trackplayer/NumberSignTypes";
import {LOCAL_PLY_KEY} from "utill/enum/localKeyEnum";
import {useTranslation} from "react-i18next";

/**
 *
 *
 *
 */
export const TrackPlayer = ({
  changeOrder,
  getPlyTrackByTrackId,
  localPlyTracks,
  changeCurrTrackInfo,
  updatePlyTrackInfo,
  changePlayLog,
  localPlayLog,
  updateSettings,
  playerSettings,
  settingsCreate,
  changePlaying,
  playingClear,
  createCurrentTrack,
  createCurrentPlayLog,
  playing,
  localPlyAddTracks,
  shuffleOrders,
  currentTrack,
  getPlyTrackByOrder,
  updateCurrPlayLog,
  updateCurrTrackInfo,
  localPly,
  localPlyCreate,
  changePlyVisible,
  removePlyByIndex,
  resetCurrTrack,
  getStatusOnLocalPly,
  localPlyActionsCreate,
  resetPlyTrack
}) => {
  const playerRef = useRef();
  const [settingsInfo, setSettingsInfo] = useState(
      playerSettings.item);
  const [localPlyInfo, setLocalPlyInfo] = useState(localPly.item);
  const [playOrders, setPlayOrders] = useState(localPly.playOrders);
  const [url, setUrl] = useState(null);
  const [trackInfo, setTrackInfo] = useState(currentTrack.info);
  const [currPlayLog, setCurrPlayLog] = useState(currentTrack.playLog);
  const [isPlaying, setIsPlaying] = useState(playing.item.playing);
  const [seeking, setSeeking] = useState(false);
  const [isEnd, setIsEnd] = useState(false);
  const [statusOnLocalPly, setStatusOnLocalPly] = useState(null);
  const {t} = useTranslation();

  const [isVisible, setVisible] = useState(localPlyTracks.isVisible);
  const variable = useRef({
    isDoubleClick: false // 더블 클릭 방지
  })

  function getLocalIndex() {
    const playLog = loadFromLocalStorage(localPlayLog.key);
    let findOrder = -1;
    if (playLog == null) {
      return 0;
    }
    const localId = playLog[0];
    const localIndex = playLog[1];
    const createdDateTime = playLog[3];
    if (localId === null || localId === -1 || localIndex === null
        || localIndex === 0) {
      return 0;
    }
    // 로컬에 있는 마지막 플레이리스트 로그 찾아서 반환
    localPly.item.map((data, index) => {
          if (data.createdDateTime === createdDateTime && data.id === localId
              && data.isStatus !== 0) {
            localPly.playOrders.map((orderIndex, order) => {
              if (index === orderIndex) {
                // playOrders 순서 에 맞게 반환
                findOrder = order;
              }
            })
          }
        }
    );
    const order = playerSettings.item.order;
    // 만약 찾지 못하였으면
    if (findOrder === -1) {
      const maxOrder = localPly.item.length;
      const isOver = (order + 1) === maxOrder;
      // 만약 넘지 않았다
      if (!isOver) {
        findOrder = order;
      } else {
        findOrder = 0;
      }
    }
    return findOrder;
  }

  useEffect(() => {
    if (localPly.userId === null) {
      return;
    }
    const findOrder = getLocalIndex();
    updateOrderAndSign(findOrder, PLUS);
    changePlayLog(findOrder);
    // 처음 접속시
    if (localPly.item.length > 0 && localPlyInfo.length === 0) {
      createCurrentTrack(findOrder, PLUS);
    }
  }, [localPly.item.length, localPly.playOrders.length]);
  // 일시정지,다음곡,이전곡,
  useEffect(() => {
    // if (currentTrack.info.id === -1) {
    //
    //   console.log(trackInfo.id);
    //   return;
    // }
    setTrackInfo(currentTrack.info);
  }, [currentTrack.info]);
  useEffect(() => {
    if (currentTrack.playLog.trackId === -1) {
      setCurrPlayLog(currentTrack.playLog);
      return;
    }
    const prevPlayToken = currPlayLog.token;
    // 정보가 있고, playLog token 이 다를 경우에만 그리고
    // 트랙아이디가 같은경우에만
    if (currentTrack.playLog.token !== prevPlayToken && currentTrack.info.id
        === currentTrack.playLog.trackId) {
      setUrl(
          `${USERS_FILE_TRACK_PLAY}${currentTrack.info.id}/${currentTrack.playLog.token}`);
    }
    setCurrPlayLog(currentTrack.playLog);
  }, [currentTrack.playLog]);

  useEffect(() => {
    if (localPly.item.length === 0) {
      setLocalPlyInfo(localPly.item);
      return;
    }
    const newStatusLocalPly = getStatusOnLocalPly();
    if (newStatusLocalPly.length === 0) {
      changePlaying(false);
      resetCurrTrack();
      removeFromLocalStorage(LOCAL_PLY_KEY);
    }

    // 현재 플레이로그 트랙아이디가 -1이 아닐 경우
    if (localPly.item.length > 0 && localPlayLog.item[0] !== -1
        && localPlyInfo.length > 0) {
      // 트랙아이디
      // const trackId = localPlayLog.item[0];
      // 플레이리스트 Index
      const prevIndex = localPlayLog.item[1];
      // 이전 인덱스의 정보 (인덱스,아이디,추가한날짜)
      const prevPly = localPlyInfo.filter(prev => prev.index === prevIndex)[0];
      // 바뀐 localPly 에서 추가한 날짜와 트랙아이디로 인덱스 바뀌었는지 확인
      localPly.item.map((item, index) => {
        // 인덱스가 다르고 트랙아이디가 같고 추가한날짜가 같을경우
        if (item.index !== prevPly.index && item.id === prevPly.id
            && item.createdDateTime === prevPly.createdDateTime) {
          playOrders.map((data, order) => {
            if (data === index) {
              // 모든 재생 순서는 playOrder 를 따른다
              // updateSettings("order", order);
              updateOrderAndSign(order, PLUS);
            }
          })
          updateCurrTrackInfo("index", item.index);
        }
      })
    }

    const findOrder = getLocalIndex();
    // 재생중이면 그대로 유지
    // 플레이 리스트에 변화가 생겼을 경우
    // 플레이
    if (statusOnLocalPly && statusOnLocalPly.length
        !== newStatusLocalPly.length) {
      if (currentTrack.info.id === -1) {
        // 이전 재생 목록 혹은 다음 재생 목록
        // 비공개가 처리가 됐을 경우 list 재 배치 후 playLog 생성
        createCurrentPlayLog(findOrder, PLUS)
      }
    }

    setLocalPlyInfo(localPly.item);

    setStatusOnLocalPly(newStatusLocalPly);
  }, [localPly.item]);

  useEffect(() => {
    setPlayOrders(localPly.playOrders);
  }, [localPly.playOrders]);

  useEffect(() => {
    if (trackInfo.id === -1) {
      return;
    }
    changeCurrTrackInfo(settingsInfo.order);
  }, [localPlyTracks.tracks]);

  useEffect(() => {
    setIsPlaying(playing.item.playing);
    setSettingsInfo(playerSettings.item);
  }, [playing.item.playing, playerSettings.item]);

  useEffect(() => {
    if (seeking) {
      changeIsChartAndLogSave(false);
    }
  }, [seeking]);

  useEffect(() => {
    changePlyVisible(isVisible);
  }, [isVisible]);

  // shuffle 여부 , index 여부
  useEffect(() => {
    if (localPlyInfo.length === 0) {
      return;
    }
    if (settingsInfo.order !== playerSettings.item.order) {
      changePlayLog(playerSettings.item.order);
    }
    // 셔플을 누를경우 Index 변환 문제로 인해
    // 리로딩 되는 문제 발생
    if (playerSettings.item.shuffle === settingsInfo.shuffle) {
      // 오더가 바뀌었는데
      // 현재 재생하고 있는 index 와 추가날짜 TrackId가 같다면
      // playLog를 불러오지 않는다
      const numberSign = playerSettings.item.numberSign;

      const plyTrackItem = getPlyTrackByOrder(playerSettings.item.order,
          numberSign);
      if (plyTrackItem) {
        const addDateTime = plyTrackItem.addDateTime;
        const createdDateTime = currentTrack.info.createdDateTime;
        const plyTrackIndex = plyTrackItem.index;
        const currTrackIndex = currentTrack.info.index;
        const plyTrackId = plyTrackItem.id;
        const currentTrackId = currentTrack.info.id;
        const eqCurrTrack = addDateTime === createdDateTime &&
            plyTrackIndex === currTrackIndex && plyTrackId === currentTrackId;
        // 같지 않을 경우 PlayLog 가져옴
        if (playing.item.playing && !eqCurrTrack) {
          createCurrentPlayLog(playerSettings.item.order,
              numberSign);
          return;
        }
      }
      createCurrentTrack(playerSettings.item.order, numberSign);
    }
    // if (currentTrack !== -1) {
    //   // const orderTrack = getPlyTrackByOrder(playerSettings.item.order);
    //   changePlayLog(currentTrack.id, playerSettings.order, new Date().getTime());
    // }
  }, [playerSettings.item.order, playerSettings.item.shuffle,
    playerSettings.item.numberSign]);

  // 일시정지,플레이버튼
  const onPlayButton = (e) => {
    e.preventDefault(); // 기본동작 정지

    if (!isPlaying && localPlyInfo.length === 0) {
      toast.error(t(`msg.player.localPly.empty`))
      return;
    }
    const trackId = Number.parseInt(e.target.dataset.id);
    if (trackId === -1) {
      return;
    }
    changePlaying(!isPlaying);
    // 같은 트랙인지 확인
    const trackEq = trackInfo.id === trackId;
    if (!isPlaying) { // 재생일 경우
      if (trackEq && currPlayLog.trackId !== -1) {// 같은 아이디에다가 PlayLog 까지 있다면 X
        return;
      }
      createCurrentPlayLog(settingsInfo.order, PLUS);
    }
  }
  let linkToTrack;
  let linkToUploader;
  const onReadyHandler = function (e) {
  }
  // 재생이 제일 먼저 시작될때
  const onStartHandler = function (e) {
    setIsEnd(false);
    setIsDoubleClick(false);
    // 0초부터 시작하지 않았다면 false
    if (settingsInfo.playedSeconds > 1 && currPlayLog.isChartLog) {
      updateCurrPlayLog("isChartLog", false);
    }
    updateSettings("playedSeconds", settingsInfo.playedSeconds);
  }

  const onEndedHandler = (e) => {
    // 전체재생
    // 전체 반복 재생
    // 무한 반복 재생
    const playBackType = playBackTypes[settingsInfo.playBackType];
    // 확인
    setIsEnd(true);

    ChartLogSave(trackInfo, currPlayLog, currPlayLog.isChartLog,
        updateCurrPlayLog);

    updateSettings("playedSeconds", 0);
    updateSettings("played", 0);

    // 현재 인덱스
    const playIndex = settingsInfo.order;

    // 전체 인덱스
    const localLength = localPlyInfo.length;

    // 다음 인덱스
    const nextIndex = playIndex + 1;

    // 마지막 인지 확인
    // 다음인덱스 전체인덱스보다 크면 true
    const isLast = nextIndex === localLength;

    // 전체 무한 재생이면서 랜덤재생이 아닐 경우
    if (playBackType === REPEAT_ALL) {
      nextTrackPlay(playIndex);
      return;
    }
    // 한곡 무한 반복
    if (playBackType === REPEAT_ONE) {
      createCurrentPlayLog(playIndex, PLUS);
      return;
    }
    // 전체 재생인데 마지막인 아닌경우
    if (!isLast && playBackType === ALL_PLAY) {
      nextTrackPlay(playIndex);
      return;
    }
    // 전체 재생인데 마지막인 경우
    if (isLast && playBackType === ALL_PLAY) {
      createCurrentTrack(playIndex, PLUS);
      changePlaying(false);
    }
  }
  const onProgressHandler = (e) => {
    if (currPlayLog.trackId === -1) {
      return;
    }
    const totalPlayTime = currPlayLog.playTime + 0.05;
    updateCurrPlayLog("playTime", totalPlayTime);
    if (!currPlayLog.isChartLog && totalPlayTime
        >= currPlayLog.miniNumPlayTime) {

      // changeIsChartAndLogSave(true)
      ChartLogSave(trackInfo, currPlayLog, currPlayLog.isChartLog,
          updateCurrPlayLog);
    }
    if (!seeking && !isEnd) {
      updateSettings("playedSeconds", e.playedSeconds);
      updateSettings("played", Number.parseInt(e.played * 100));
      updateSettings("loaded", Number.parseInt(e.loaded * 100));
    }
  }
  const onDurationHandler = (e) => {
    playerRef.current.seekTo(settingsInfo.playedSeconds, 'seconds');
  }

  function setState(param) {
  }

  const onErrorHandler = (e) => {
    createCurrentPlayLog(settingsInfo.order, PLUS);
  }
  const getPlayButton = (playing) => {
    return playing ? 'play-pause-btn-paused' : 'play-pause-btn';
  }
  const getMuteButton = (muted) => {
    return muted ? 'mute-volume-btn-muted' : 'mute-volume-btn';
  }
  const divRef = useRef(null);
  const onMouseUp = (event) => {
    if (!divRef.current) {
      return;
    }
    const rect = divRef.current.getBoundingClientRect();  // div의 위치와 크기 정보
    const width = rect.width;
    const relativeX = event.clientX - rect.left;  // 상대적 X 위치 계산
    // 전체 길이에서 percent;
    const percent = (relativeX / width) * 100;
    // 전체 길이에서 percent 를 곱한 값
    const seekToSeconds = trackInfo.trackLength * (percent / 100);
    if (playerRef.current) {
      playerRef.current.seekTo(seekToSeconds, 'seconds');
      updateSettings("playedSeconds", seekToSeconds);
      updateSettings("played", percent);
      setSeeking(false);// 재생이동 중
    }
    // 원한다면 상태를 업데이트 하거나 화면에 표시할 수 있습니다.
  };
  const onMouseDown = () => {
    setSeeking(true);// 재생이동 중
  }
  const onMouseMove = (event) => {
    if (!seeking) {
      return;
    }
    if (!divRef.current) {
      return;
    }
    const rect = divRef.current.getBoundingClientRect();  // div의 위치와 크기 정보
    const width = rect.width;
    const relativeX = event.clientX - rect.left;  // 상대적 X 위치 계산
    // 전체 길이에서 percent;
    const percent = (relativeX / width) * 100;
    // 전체 길이에서 percent 를 곱한 값
    const seekToSeconds = trackInfo.trackLength * (percent / 100);
    updateSettings("playedSeconds", seekToSeconds);
    updateSettings("played", percent);
  }

  function updateOrderAndSign(changeIndex, numberSign) {
    updateSettings("order", changeIndex);
    updateSettings("numberSign", numberSign);
  }

  const onPreBtnClick = () => {
    if (variable.current.isDoubleClick) {
      return;
    }
    setIsDoubleClick(true);
    changeIsChartAndLogSave(false);
    const localLength = localPlyInfo.length;

    const maxIndex = localLength === 0 ? localLength : localLength - 1;

    const prevIndex = settingsInfo.order - 1;

    const changeIndex = prevIndex < 0 ? maxIndex : prevIndex;

    if (playerRef.current) {
      playerRef.current.seekTo(0, "seconds");
    }
    if (!isPlaying) {
      changePlaying(!isPlaying);
    }
    setIsDoubleClick(false);
    updateSettings("playedSeconds", 0);
    // 5초보다 크다면 이전곡이 아닌 0초부터 시작
    if (settingsInfo.playedSeconds > 5) {
      createCurrentPlayLog(settingsInfo.order, MINUS);
      return;
    }
    // 하나만 있을경우
    if (playerSettings.order === changeIndex) {
      createCurrentPlayLog(changeIndex, MINUS);
      return;
    }
    updateOrderAndSign(changeIndex, MINUS);
  }

  function nextTrackPlay(playIndex) {
    const totalLength = localPlyInfo.length;
    const minIndex = 0;
    const nextIndex = playIndex + 1;
    const changeIndex = nextIndex === totalLength ? minIndex : nextIndex;
    if (playerRef.current) {
      playerRef.current.seekTo(0, "seconds");
    }
    if (playIndex === changeIndex) {
      createCurrentPlayLog(playIndex, PLUS);
    } else {
      updateOrderAndSign(changeIndex, PLUS);
    }
    updateSettings("playedSeconds", 0);
    if (!isPlaying) {
      changePlaying(!isPlaying);
    }
  }

  function changeIsChartAndLogSave(isChartLog) {
    if (currPlayLog.trackId === -1) {
      return;
    }
    ChartLogSave(trackInfo, currPlayLog, isChartLog, updateCurrPlayLog);
  }

  const nextBtnOnClick = () => {
    if (variable.current.isDoubleClick) {
      return;
    }
    setIsDoubleClick(true);
    changeIsChartAndLogSave(false);
    nextTrackPlay(settingsInfo.order);
    setIsDoubleClick(false);

  }

  const changePlayBackType = (e) => {
    const maxIndex = playBackTypes.length - 1;
    const currentIndex = Number.parseInt(e.target.dataset.id);

    const changeIndex = currentIndex === maxIndex ? 0 : currentIndex + 1;

    updateSettings("playBackType", changeIndex);
  }

  const changeShuffleType = () => {
    const isShuffle = !settingsInfo.shuffle;
    // 랜덤 재생에서
    // 뒤로가기를 눌렀을 경우
    // 이전에 들었던곡이 없다면 랜덤곡
    // 있다면?
    // 그 이전곡 플레이 만약
    // 그 이전곡에서 다음 플레이를 누르면 뒤로가기전 플레이곡

    updateSettings("shuffle", isShuffle);

    const shuffleArray = shufflePlayOrder(playOrders, isShuffle,
        localPlyInfo,
        settingsInfo.order);
    shuffleOrders(shuffleArray);

    if (isShuffle) {
      updateOrderAndSign(0, PLUS);
      return;
    }
    // 기본 정렬된 Index 반환
    // const order = playOrders[];
    updateOrderAndSign(trackInfo.index - 1, PLUS);
  }
  // 일시정지시
  const onPauseHandler = (e) => {
    if (currPlayLog.trackId === -1) {
      return;
    }
    changeIsChartAndLogSave(false);
  }

  function setIsDoubleClick(isDoubleClick) {
    variable.current.isDoubleClick = isDoubleClick;
  }

  const toggleLike = (id) => {
    localPlyAddTracks(1);
    localPlyAddTracks(3);
    localPlyAddTracks(4);
    localPlyAddTracks(5);
    localPlyAddTracks(6);
    localPlyAddTracks(6);
    localPlyAddTracks(8);
    localPlyAddTracks(9);
    localPlyAddTracks(10);
    localPlyAddTracks(11);

    if (id === -1) {
      return;
    }
    if (variable.current.isDoubleClick) {
      return;
    }
    const trackInfo = getPlyTrackByTrackId(id);
    setIsDoubleClick(true);
    ToggleLike(trackInfo.id, trackInfo.title, trackInfo.isLike,
        updatePlyTrackInfo, t);
    setIsDoubleClick(false);
  }
  const toggleFollow = () => {
    if (variable.current.isDoubleClick) {
      return;
    }
    setIsDoubleClick(true);
    ToggleFollow(trackInfo.id, trackInfo.postUser, updatePlyTrackInfo, t);
    setIsDoubleClick(false);
  }

  const changeIsVisible = (value) => {
    setVisible(value);
  }
  const plyButtonClickHandler = () => {

    changeIsVisible(!isVisible);
  }

  const playListProps = {
    changeOrder,
    getPlyTrackByTrackId,
    isVisible,
    changeIsVisible,
    isPlaying,
    playOrders,
    localPlyInfo,
    trackInfo,
    updateSettings,
    settingsInfo,
    localPlayLog,
    changePlayLog,
    changePlaying,
    currPlayLog,
    createCurrentPlayLog,
    playerRef,
    toggleLike,
    shuffleOrders,
    updateCurrTrackInfo,
    removePlyByIndex,
    resetCurrTrack,
    variable,
    updateOrderAndSign,
    setIsDoubleClick,
    resetPlyTrack,
    updateCurrPlayLog,
    t
  }
  return (
      <div id='track-player-bar'>
        <div id='track-player-container'>
          <PlayList
              {...playListProps}
          />
          <div id='tp-controller'>
            <div id='previous-btn'
                 className='controller-btn' onClick={onPreBtnClick}></div>
            <div id={getPlayButton(isPlaying)}
                 data-id={trackInfo.id}
                 className='controller-btn'
                 onClick={(e) => onPlayButton(e)}></div>
            <div id='next-btn' className='controller-btn'
                 onClick={nextBtnOnClick}></div>
            <div onClick={changeShuffleType}
                 className={'controller-btn bg_player shuffle-btn '
                     + (settingsInfo.shuffle && 'active')}></div>
            <div data-id={settingsInfo.playBackType}
                 onClick={changePlayBackType}
                 className={'bg_player controller-btn '
                     + playBackTypes[settingsInfo.playBackType]}></div>
          </div>
          <div id='tp-progress'>
            <div id='tp-timepassed'>{secondsToTime(
                settingsInfo.playedSeconds)}</div>
            <div style={{width: '500px'}} ref={divRef} onMouseDown={onMouseDown}
                 onMouseOver={onMouseMove}
                 onMouseUp={onMouseUp} id='tp-scrubbar'
                 data-seconds={settingsInfo.played}>
              <div id='scrub-bg'></div>
              <div id='scrub-progress'
                   style={{width: settingsInfo.played + `%`}}></div>
              <div id='scrup-handle'
                   style={{left: settingsInfo.played + `%`}}></div>
            </div>
            <div id='tp-duration'>{durationTime(trackInfo.trackLength)}</div>
          </div>
          <div className='tp-track-dets'>

            <div className="volume_btn">
              <div id={getMuteButton(settingsInfo.muted)}
                   className='controller-btn'
                   onClick={() => updateSettings("muted", !settingsInfo.muted)}>
              </div>
              <input
                  id="volumeControl"
                  type="range"
                  className="vertical-slider"
                  min="0"
                  max="1"
                  step="0.01"
                  value={settingsInfo.muted ? 0 : settingsInfo.volume}
                  onChange={(e) => updateSettings("volume", e.target.value)}
              />
            </div>

            <div className='tp-td-uploader-pic'>
              <a href={linkToTrack}> <img className="player_cover_img"
                                          src={trackInfo.coverUrl
                                              && USERS_FILE_IMAGE
                                              + trackInfo.coverUrl}
                                          alt={""}/></a>
            </div>
            <div className='tp-td-track-info text-start'>
              <a href={linkToUploader}><p
                  className='tp-track-uploader'>{trackInfo.postUser.userName}</p>
              </a>
              <a href={linkToTrack}><p
                  className='tp-track-name'>{trackInfo.title}</p>
              </a>
            </div>
            {!trackInfo.isOwner && <>
              <div className={'controller-btn ' + (trackInfo.isLike
                  ? 'liked-button-t' : 'liked-button')}
                   data-id={currentTrack.info.id}
                   onClick={(e) => toggleLike(currentTrack.info.id)}></div>
              <div className={'controller-btn bg_player ' + (
                  trackInfo.postUser.isFollow ? 'follow-button-t'
                      : 'follow-button')}
                   onClick={(e) => toggleFollow(e)}></div>
            </>}
            <div className={'controller-btn bg_player ' + (
                isVisible ? 'ply-button-t'
                    : 'ply-button')} onClick={plyButtonClickHandler}>
            </div>
          </div>
        </div>
        <ReactPlayer
            ref={playerRef}
            width='100%'
            height='0%'

            url={url}
            playing={isPlaying} // 재생 여부 기본 : false
            onError={onErrorHandler}
            loop={settingsInfo.playbackRate === REPEAT_ONE} // 반복재생 여부 false
            volume={Number.parseFloat(settingsInfo.volume)} // 볼륨값 기본 Null
            muted={settingsInfo.muted} // 음소거 여부
            onStart={onStartHandler}
            //일시중지 또는 버퍼링 후 미디어 재생이 시작되거나 재개될 때 호출됩니다
            progressInterval={50}
            //onProgress콜백 사이의 시간 (밀리초)
            onEnded={onEndedHandler}

            //미디어 재생이 끝나면 호출됩니다 . ◦ 다음으로 설정된
            //   경우 실행되지 않습니다 .looptrue
            onProgress={onProgressHandler}
            onPause={onPauseHandler}
            // 콜백을 포함하고 played 분수 및 초 단위 loaded 로 진행   ◦ 예:playedSecondsloadedSeconds
            /*{ played: 0.12, playedSeconds: 11.3, loaded: 0.34, loadedSeconds: 16.7 }*/
            onDuration={onDurationHandler}// 미디어 지속 시간(초)을 포함하는 콜백
            onReady={onReadyHandler}//미디어가 로드되고 재생할 준비가 되면 호출됩니다.
        />
      </div>
  );
};

