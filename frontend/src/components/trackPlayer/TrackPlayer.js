import ReactPlayer from 'react-player';
import {useEffect, useRef, useState} from "react";
//


function TrackPlayer({setTrackPlayer, trackId, trackplayer, setPlayPause, player, endCurrentTrack,
  currentTrack, playing, toggleLike, liked}) {
  const [state, setState] = useState({
    volume: 0.8, // 플레이어의 볼륨을 과 사이에서 설정하고 0모든 플레이어의 기본 볼륨을 사용합니다 1
    muted: false, // 플레이 음소거 설정
    played: 0, //
    playedSeconds: 0,
    loaded: 0,
    duration: 0, // 지속시간
    playbackRate: 1.0,
    loop: false,
    startT: null,
  });


  const prevProps = useRef({setTrackPlayer, trackId, trackplayer, setPlayPause, player, endCurrentTrack,
    currentTrack, playing, toggleLike, liked});
  const playerRef = useRef(null);

  useEffect(() => {
    if (trackId === -1) return;
    if (trackId !== prevProps.current.trackId) {
      let progress = trackplayer.waveSeek || 0;
      // let progress = 0;
      // 재생 정보 초기화
      setState(prevState => ({...prevState, startT: progress}));

      player.seekTo(progress * state.duration);
    } else if (trackplayer.waveSeek !== prevProps.current.trackplayer.waveSeek) {
      let seek = trackplayer.progressTrackId[trackId] * state.duration;
      player.seekTo(seek);
    }
  }, [trackId, trackplayer]);

  useEffect(() => {
    if (state.startT !== null) {
      const startTime = state.startT;
      setState((prevState) => {
        return ({...prevState, startT: 0});
      });
      player.seekTo(startTime * state.duration);
    }
  }, [state.startT]);

  // componentWillReceiveProps(newProps){
  //   if (newProps.seek !== this.props.seek ){
  //     this.reactplayer.current.seekTo(newProps.seek);
  //   }

  //   // if (this.props.currentUser.liked !== newProps.currentUser.liked){
  //   //   this.props.fetchTrack(newProps.match.params.id);
  //   // }
  // }

  // 지속
  // 미디어 지속 시간(초)을 포함하는 콜백
  const onDuration = (duration) => {
    setState((prevState) => {
      return ({...prevState, duration});
    });
  };


  // 진행중
  const onProgress = (progressState) => {
    if (!state.seeking) {
      setState((prevState) => {
        return ({...prevState, ...progressState});
      });
    }
  };

  // 일시 중지
  const playPause = (e) => {
    e.preventDefault();
    let trackProg = trackplayer.progressTrackId[trackId];
    let prog = trackProg || playerRef.current.getCurrentTime() / playerRef.current.getDuration();
    setPlayPause(!playing, trackId, prog);
  };


  const onEnded = () => {
    endCurrentTrack(trackId);
  };

  const toggleMute = () => {
    setState((prevState) => {
      return ({...prevState, muted: !prevState.muted});
    });
  };


  const secondsToTime = (seconds) => {
    let duration = new Date(null);
    duration.setSeconds(seconds);
    return duration.toISOString().substr(14, 5);
  };
  //
  // testFunction(){
  //
  //   if (!this.props.currentTrack){
  //     return {
  //       trackToPlay: '',
  //       trackImage: 'https://image.flaticon.com/icons/svg/3/3722.svg',
  //       trackUploader: '',
  //       trackName: '',
  //       likeButton: 'liked-button',
  //       linkToTrack: `/#/tracks`,
  //       linkToUploader: '/#/tracks'
  //     };} else {
  //     let liked;
  //     let cTrack = this.props.currentTrack;
  //     if (this.props.liked){
  //       liked = 'liked-button-t';}else{ liked = 'liked-button';}
  //     return {
  //       trackToPlay: cTrack.audioUrl,
  //       trackImage: cTrack.imageUrl,
  //       trackUploader: cTrack.uploader,
  //       trackName: cTrack.title,
  //       likeButton: liked,
  //       linkToTrack: `/#/tracks/${cTrack.id}`,
  //       linkToUploader: `/#/users/${cTrack.uploaderId}`
  //     };
  //   }
  // }


  // let {loop, volume, muted} = state;
  let {
    trackToPlay,
    trackImage,
    trackUploader,
    trackName,
    likeButton,
    linkToTrack,
    linkToUploader
  } = this.testFunction();

  let playButton = (playing) ?
      'play-pause-btn-paused' : 'play-pause-btn';

  let muteButton = (state.muted) ?
      'mute-volume-btn-muted' : 'mute-volume-btn';

  let durationTime = secondsToTime(state.duration);
  let playedTime = secondsToTime(state.playedSeconds);
  let percentage = `${Math.ceil(state.played * 100)}%`;
  // let loopActive = loop ? 'loop-btn-active' : 'loop-btn';

  return (
      <div id='track-player-bar'>
        <div id='track-player-container'>
          <div id='tp-controller'>
            <div id='previous-btn'
                 className='controller-btn non-active-btn'></div>
            <div id={playButton} className='controller-btn'
                 onClick={(e) => playPause(e)}></div>
            <div id='next-btn' className='controller-btn non-active-btn'></div>
            <div className='shuffle-btn controller-btn non-active-btn'></div>
            <div className='loop-btn controller-btn non-active-btn'
                 onClick={() => setState({...state,loop: !state.loop})}></div>
          </div>
          <div id='tp-progress'>
            <div id='tp-timepassed'>{playedTime}</div>
            <div id='tp-scrubbar'>
              <div id='scrub-bg'></div>
              <div id='scrub-progress' style={{width: percentage}}></div>
              <div id='scrup-handle'></div>
            </div>
            <div id='tp-duration'>{durationTime}</div>
          </div>
          <div className='tp-track-dets'>
            <div id={muteButton} className='controller-btn'
                 onClick={() => setState({...state,muted: !state.muted})}></div>
            <div className='tp-td-uploader-pic'>
              <a href={linkToTrack}><img src={trackImage}/></a>
            </div>
            <div className='tp-td-track-info'>
              <a href={linkToUploader}><p
                  className='tp-trackuploader'>{trackUploader}</p></a>
              <a href={linkToTrack}><p className='tp-trackname'>{trackName}</p>
              </a>
            </div>
            <div id={likeButton} className='controller-btn'
                 onClick={(e) => toggleLike(currentTrack.id, e)}></div>
            <div id='playlist-button' className='controller-btn'></div>

          </div>
        </div>
        <ReactPlayer
            // ref={ref}
            width='0%'
            height='0%'
            url={process.env.PUBLIC_URL + "/users/file/track/play/6/373c76f62063e6ae84a8"}
            playing={playing}
            loop={state.loop}
            volume={state.volume}
            muted={state.muted}

            progressInterval={50}
            onEnded={() => onEnded}
            onProgress={onProgress}
            onDuration={onDuration}
            // onReady={() => keepProgress}
        />
      </div>
  );

}

export default TrackPlayer;
