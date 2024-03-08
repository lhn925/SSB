
import React, { useState, useEffect, useRef } from 'react';
import ReactPlayer from 'react-player';

const TrackPlayer = ({ setTrackPlayer, trackId, trackplayer, setPlayPause, player, endCurrentTrack,
    currentTrack, playing, toggleLike, liked }) => {
    const [state, setState] = useState({
        volume: 0.8,
        muted: false,
        played: 0,
        playedSeconds: 0,
        loaded: 0,
        duration: 0,
        playbackRate: 1.0,
        loop: false,
        startT: null,
    });

    const playerRef = useRef(null);

    useEffect(() => {
        if (trackId === -1) return;

        if (trackId !== prevProps.trackId) {
            let progress = trackplayer.waveSeek || 0;
            setState((prevState) => ({ ...prevState, startT: progress }));
            player.seekTo(progress * state.duration);
        } else if (trackplayer.waveSeek !== prevProps.trackplayer.waveSeek) {
            let seek = trackplayer.progressTrackId[trackId] * state.duration;
            player.seekTo(seek);
        }
    }, [trackId, trackplayer]);

    useEffect(() => {
        if (state.startT !== 0) {
            const startTime = state.startT;
            setState((prevState) => ({ ...prevState, startT: 0 }));
            player.seekTo(startTime * state.duration);
        }
    }, [state.startT]);

    const onDuration = (duration) => {
        setState((prevState) => ({ ...prevState, duration }));
    };

    const onProgress = (progressState) => {
        if (!state.seeking) {
            setState((prevState) => ({ ...prevState, ...progressState }));
        }
    };

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
        setState((prevState) => ({ ...prevState, muted: !prevState.muted }));
    };

    const secondsToTime = (seconds) => {
        let duration = new Date(null);
        duration.setSeconds(seconds);
        return duration.toISOString().substr(14, 5);
    };

    // Additional methods and UI rendering logic...

    return (
        <div id='track-player-bar'>
            {/* UI components and ReactPlayer usage */}
            <ReactPlayer
                ref={playerRef}
                width='0%'
                height='0%'
                url={trackToPlay}
                playing={playing}
                loop={state.loop}
                volume={state.volume}
                muted={state.muted}
                progressInterval={50}
                onEnded={onEnded}
                onProgress={onProgress}
                onDuration={onDuration}
                onReady={() => {/* Handle player ready */}}
            />
        </div>
    );
};

export default TrackPlayer;
