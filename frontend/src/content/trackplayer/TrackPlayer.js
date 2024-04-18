import React, {useEffect, useRef, useState} from 'react';
import 'css/playerBar/playerBar.css';
import 'css/track/track.css';
import ReactPlayer from "react-player";

import {USERS_FILE_IMAGE, USERS_FILE_TRACK_PLAY} from "utill/api/ApiEndpoints";
import {durationTime, secondsToTime} from "utill/function";
import {REPEAT_ONE} from "utill/enum/PlaybackTypes";

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
  createCurrentTrack
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
  // 플레이 시간
  const [url, setUrl] = useState(null);

  const [trackInfo, setTrackInfo] = useState(currentTrack);

  const [isPlaying, setIsPlaying] = useState(playing.item.playing);
  const [seeking, setSeeking] = useState(false);

  useEffect(() => {
    setTrackInfo(currentTrack);
    if (currentTrack && currentTrack.playLog) {
      setUrl(
          `${USERS_FILE_TRACK_PLAY}${currentTrack.id}/${currentTrack.playLog.token}`);
    }
  }, [currentTrack]);
  useEffect(() => {
    setLocalPlyInfo(localPly.item);
  }, [localPly.item]);

  useEffect(() => {
    setIsPlaying(playing.item.playing);
    setSettingsInfo(playerSettings.item);

  }, [playing.item.playing, playerSettings.item, localPlyInfo]);

  useEffect(() => {
    if (localPlyInfo.length > 0) {
      if (playing.item.playing) {
        updateCurrentTrack(localPlyInfo[playerSettings.item.index].id);
        return;
      }
      createCurrentTrack(localPlyInfo[playerSettings.item.index]);
    }
  }, [playerSettings.item.index, localPlyInfo]);

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
    updateSettings("playedSeconds", settingsInfo.playedSeconds);
  }

  const onEnded = (e) => {
    // 종료시에 전체 길이 저장
    // updateSettings("playedSeconds", trackInfo.trackLength);
  }

  const onProgress = (e) => {
    if (!seeking) {
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
    // 5초보다 크다면 이전곡이 아닌 0초부터 시작
    if (settingsInfo.playedSeconds > 5) {
      updateSettings("playedSeconds", 0);
      updateCurrentTrack(trackInfo.id);
      return;
    }
    // const prevTrack = localPly.item(changeIndex);
    // updateCurrentTrack(prevTrack.id);
    updateSettings("index", changeIndex);
  }
  const nextBtnOnClick = (event) => {
    localPlyAddTracks(1);
    localPlyAddTracks(2);
    localPlyAddTracks(3);
    localPlyAddTracks(4);
    localPlyAddTracks(5);
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
            <div className='shuffle-btn controller-btn'></div>
            <div id="loop-btn-active"
                 className='loop-btn controller-btn'></div>
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
                  value={settingsInfo.volume}
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

