// playbackTypes.js
export const ALL_PLAY = 'allPlay'; // 전체 재생
export const REPEAT_ONE = 'repeatOne'; // 한곡 반복 재생
export const REPEAT_ALL = 'repeatAll'; // 전체 반복 재생
export const SHUFFLE = 'shuffle';
export const NEXT = 'next';
export const QUEUE = 'queue';
export const CONTINUOUS_PLAY = 'continuousPlay';

const repeatTypes = {
  ALL_PLAY,REPEAT_ONE,REPEAT_ALL
}

export const playBackTypes = Object.values(repeatTypes);

