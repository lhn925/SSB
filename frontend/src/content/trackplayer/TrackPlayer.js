import React, {useEffect, useRef, useState} from 'react';
import 'css/playerBar/playerBar.css';
import 'css/track/track.css';
import ReactPlayer from "react-player";

import {USERS_FILE_IMAGE, USERS_FILE_TRACK_PLAY} from "utill/api/ApiEndpoints";
import {durationTime, secondsToTime} from "utill/function";
import {
  AUTO_PLAY,
  playBackTypes,
  REPEAT_ALL,
  REPEAT_ONE
} from "utill/enum/PlaybackTypes";

/**
 *
 *
 *
 */

// url={trackToPlay} // 재생할 url
// playing={playing} // 재생 여부 기본 : false
// loop={loop} // 반복재생 여부 false
// volume={volume} // 볼륨값 기본 Null
// muted={muted} // 음소거 여부
//
// progressInterval={50}
// //onProgress콜백 사이의 시간 (밀리초)
// onEnded={() => onEnded()}
// //미디어 재생이 끝나면 호출됩니다 . ◦ 다음으로 설정된
// //   경우 실행되지 않습니다 .looptrue
//
// onProgress={onProgress()}
//
// // 콜백을 포함하고 played 분수 및 초 단위 loaded 로 진행   ◦ 예:playedSecondsloadedSeconds
// {/*{ played: 0.12, playedSeconds: 11.3, loaded: 0.34, loadedSeconds: 16.7 }*/}
// onDuration={onDuration()}// 미디어 지속 시간(초)을 포함하는 콜백
// onReady={() => keepProgress()}//미디어가 로드되고 재생할 준비가 되면 호출됩니다.
export const TrackPlayer = ({
  changePlaying,
  playing,
  currentTrack,
  updateCurrentTrack,
  playerSettings,
  updateSettings,
  localPly,
  localPlyAddTracks,
  createCurrentTrack,
  shuffleOrders
}) => {
  const playerRef = useRef();
  // 현재 재생목록
  // 앞으로가기
  // 뒤로가기
  // 재생 정지
  // 재생
  // 볼륨
  // 좋아요
  // 랜덤재생
  // 프로필 이동
  // 재생바
  // 현재 재생 목록 -> 로컬스토리지에 저장 멜론과 비슷한 형태

  const [settingsInfo, setSettingsInfo] = useState(
      playerSettings.item);

  const [localPlyInfo, setLocalPlyInfo] = useState(localPly.item);
  const [playOrders, setPlayOrders] = useState(localPly.playOrders);
  // 플레이 시간
  const [url, setUrl] = useState(null);

  const [trackInfo, setTrackInfo] = useState(currentTrack);

  const [isPlaying, setIsPlaying] = useState(playing.item.playing);
  const [seeking, setSeeking] = useState(false);
  const [isEnd, setIsEnd] = useState(false);
  const [isLast, setIsLast] = useState(false);

  useEffect(() => {
    setTrackInfo(currentTrack);
    if (currentTrack && currentTrack.playLog) {
      setUrl(
          `${USERS_FILE_TRACK_PLAY}${currentTrack.id}/${currentTrack.playLog.token}`);
    }
  }, [currentTrack]);
  useEffect(() => {
    setLocalPlyInfo(localPly.item);
    setPlayOrders(localPly.playOrders);
    const playBackType = playBackTypes[settingsInfo.playBackType];
    //마지막이면서 전체 반복재생인경우

    const lastAndShuffle = isLast && settingsInfo.shuffle;
    if (playBackType === REPEAT_ALL && lastAndShuffle) {
      updateSettings("index", 1);
      updateCurrentTrack(localPly.item[[localPly.playOrders[1]]].id);
      return;
    }

    // 마지막이면서 전체재생인 경우
    if (isLast && playBackType === AUTO_PLAY) {
      if (isPlaying) { // 재생인 경우에만 false 로 바꿔줌
        changePlaying();
      }
      if (settingsInfo.shuffle) { // shuffle 인 경우 0번째 인덱스로
        updateSettings("index", 0);
      }
      createCurrentTrack(trackInfo);
    }

  }, [localPly.item, localPly.playOrders, isLast]);

  useEffect(() => {
    setIsPlaying(playing.item.playing);
    setSettingsInfo(playerSettings.item);

  }, [playing.item.playing, playerSettings.item, localPlyInfo]);

  useEffect(() => {
    // 셔플이 바뀐 경우에는 x
    if (playerSettings.item.shuffle !== settingsInfo.shuffle) {
      return;
    }
    if (localPlyInfo.length > 0) {
      if (playing.item.playing) {
        updateCurrentTrack(
            localPlyInfo[playOrders[playerSettings.item.index]].id);
        return;
      }
      createCurrentTrack(localPlyInfo[playOrders[playerSettings.item.index]]);
    }
  }, [playerSettings.item.index, localPlyInfo, playerSettings.item.shuffle]);

  // 일시정지,플레이버튼
  const playPause = (e) => {
    e.preventDefault(); // 기본동작 정지
    changePlaying();
    const trackId = Number.parseInt(e.target.dataset.id);
    if (trackId === -1) {
      return;
    }
    // 같은 트랙인지 확인
    const trackEq = trackInfo.id === trackId;
    if (!isPlaying) { // 재생일 경우
      const playLog = trackInfo.playLog;
      if (trackEq && playLog) {// 같은 아이디에다가 PlayLog 까지 있다면 X
        return;
      }
      updateCurrentTrack(currentTrack.id);
    } else {

    }
  }

  let linkToTrack;
  let linkToUploader;

  let likeButton = 'liked-button-t';
  let followButton = 'liked-button-t';
  const toggleLike = function (id, e) {

  }

  const onReady = function (e) {
    console.log("hi");
  }
  // 재생이 제일 먼저 시작될때
  const onStart = function (e) {
    setIsEnd(false);
    setIsLast(false);
    updateSettings("playedSeconds", settingsInfo.playedSeconds);
  }

  const onEnded = (e) => {
    // 전체재생
    // 전체 반복 재생
    // 무한 반복 재생
    const playBackType = playBackTypes[settingsInfo.playBackType];
    // 확인
    setIsEnd(true);
    updateSettings("playedSeconds", 0);
    updateSettings("played", 0);

    // 현재 인덱스
    const playIndex = settingsInfo.index;

    // 전체 인덱스
    const localLength = localPlyInfo.length - 1;

    // 다음 인덱스
    const nextIndex = playIndex + 1;

    // 마지막 인지 확인
    // 다음인덱스 전체인덱스보다 크면 true
    const isLast = nextIndex > localLength;

    setIsLast(isLast);

    // 전체 무한 재생이면서 랜덤재생이 아닐 경우
    if (playBackType === REPEAT_ALL && !settingsInfo.shuffle) {
      nextTrackPlay(playIndex);
      return;
    }

    // 한곡 무한 반복
    if (playBackType === REPEAT_ONE) {
      updateCurrentTrack(trackInfo.id);
      return;
    }

    // 마지막 이면서 shuffle인경우
    if (isLast && settingsInfo.shuffle) {
      shuffleOrders(settingsInfo.shuffle);
    }
    // 마지막 재생목록인지 확인
    // const isLast = nextIndex > localLength ? minIndex : nextIndex;

  }

  const onProgress = (e) => {
    if (!seeking && !isEnd) {
      updateSettings("playedSeconds", e.playedSeconds);
      updateSettings("played", Number.parseInt(e.played * 100));
      updateSettings("loaded", Number.parseInt(e.loaded * 100));
    }
  }

  const onDuration = (e) => {
    playerRef.current.seekTo(settingsInfo.playedSeconds, 'seconds');
  }

  function setState(param) {
  }

  const onError = (e) => {
    console.log("error")
    updateCurrentTrack(trackInfo.id);
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
  const onMouseDown = (event) => {
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
  const preBtnOnClick = () => {
    const localLength = localPlyInfo.length;

    const maxIndex = localLength === 0 ? localLength : localLength - 1;

    const prevIndex = settingsInfo.index - 1;

    const changeIndex = prevIndex < 0 ? maxIndex : prevIndex;

    if (playerRef.current) {
      playerRef.current.seekTo(0, "seconds");
    }
    updateSettings("playedSeconds", 0);
    // 5초보다 크다면 이전곡이 아닌 0초부터 시작
    if (settingsInfo.playedSeconds > 5) {
      updateCurrentTrack(trackInfo.id);
      return;
    }
    updateSettings("index", changeIndex);
  }

  function nextTrackPlay(playIndex) {
    const localLength = localPlyInfo.length - 1;

    const minIndex = 0;

    const nextIndex = playIndex + 1;

    const changeIndex = nextIndex > localLength ? minIndex : nextIndex;
    if (playerRef.current) {
      playerRef.current.seekTo(0, "seconds");
    }
    updateSettings("playedSeconds", 0);
    updateSettings("index", changeIndex);
  }

  const nextBtnOnClick = () => {
    nextTrackPlay(settingsInfo.index);
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
    // 인덱스 0 에서 눌렀다? 랜덤재생 대신 인덱스 원래 위치대로 아니다

    shuffleOrders(isShuffle);
    updateSettings("shuffle", isShuffle);
    if (isShuffle) {
      updateSettings("index", 0);
      return;
    }
    // 기본 정렬된 Index 반환
    const index = playOrders[settingsInfo.index];
    updateSettings("index", index);

  }
  return (
      <div id='track-player-bar'>
        <div id='track-player-container'>
          <div id='tp-controller'>
            <div id='previous-btn'
                 className='controller-btn' onClick={preBtnOnClick}></div>
            <div id={getPlayButton(isPlaying)}
                 data-id={trackInfo.id}
                 className='controller-btn'
                 onClick={(e) => playPause(e)}></div>
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
              <a href={linkToTrack}><img className="player_cover_img"
                                         src={trackInfo.coverUrl
                                             && USERS_FILE_IMAGE
                                             + trackInfo.coverUrl}
                                         alt={""}/></a>
            </div>
            <div className='tp-td-track-info text-start'>
              <a href={linkToUploader}><p
                  className='tp-track-uploader'>{trackInfo.userName}</p></a>
              <a href={linkToTrack}><p
                  className='tp-track-name'>{trackInfo.title}</p>
              </a>
            </div>
            <div id={likeButton} className='controller-btn'
                 onClick={(e) => toggleLike(currentTrack.id, e)}></div>
            <div id={followButton} className='controller-btn'
                 onClick={(e) => toggleLike(currentTrack.id, e)}></div>
            <div id='playlist-button' className='controller-btn'></div>
          </div>
        </div>

        <ReactPlayer
            ref={playerRef}
            width='100%'
            height='0%'
            url={url}
            // url={getUrl()}
            // url={"/users/file/track/play/202/124a25c32864139a2149"} // 재생할 url
            playing={isPlaying} // 재생 여부 기본 : false
            onError={onError}
            loop={settingsInfo.playbackRate === REPEAT_ONE} // 반복재생 여부 false
            volume={Number.parseFloat(settingsInfo.volume)} // 볼륨값 기본 Null
            muted={settingsInfo.muted} // 음소거 여부
            onStart={onStart}
            //일시중지 또는 버퍼링 후 미디어 재생이 시작되거나 재개될 때 호출됩니다
            progressInterval={50}
            //onProgress콜백 사이의 시간 (밀리초)
            onEnded={onEnded}
            //미디어 재생이 끝나면 호출됩니다 . ◦ 다음으로 설정된
            //   경우 실행되지 않습니다 .looptrue
            onProgress={onProgress}
            // 콜백을 포함하고 played 분수 및 초 단위 loaded 로 진행   ◦ 예:playedSecondsloadedSeconds
            /*{ played: 0.12, playedSeconds: 11.3, loaded: 0.34, loadedSeconds: 16.7 }*/
            onDuration={onDuration}// 미디어 지속 시간(초)을 포함하는 콜백
            onReady={onReady}//미디어가 로드되고 재생할 준비가 되면 호출됩니다.
        />
      </div>
  );
};

