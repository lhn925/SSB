export const TypeNames = {
  PlyTypes: "playListType", MusicTypes: "genre", GenreType: "genreType"
};

export const PlyTypes = {
  PLAYLIST: {name: TypeNames.PlyTypes, value: "Playlist", sub: false},
  ALBUM: {name: TypeNames.PlyTypes, value: "Album", sub: false},
  EP: {name: TypeNames.PlyTypes, value: "EP", sub: false},
  SINGLE: {name: TypeNames.PlyTypes, value: "Single", sub: false},
  COMPILATION: {
    name: TypeNames.PlyTypes,
    value: "Compilation",
    sub: false
  },
};



const SubMusicTypes = {
  ALTERNATIVE_ROCK: {name: "MUSIC", value: "alternative_rock"},
  AMBIENT: {name: "MUSIC", value: "ambient"},
  CLASSICAL: {name: "MUSIC", value: "classical"},
  COUNTRY: {name: "MUSIC", value: "country"},
  DANCE_EDM: {name: "MUSIC", value: "dance_edm"},
  DANCEHALL: {name: "MUSIC", value: "dancehall"},
  DEEP_HOUSE: {name: "MUSIC", value: "deep_house"},
  DISCO: {name: "MUSIC", value: "disco"},
  DRUM_BASS: {name: "MUSIC", value: "drum_bass"},
  DUBSTEP: {name: "MUSIC", value: "dubstep"},
  ELECTRONIC: {name: "MUSIC", value: "electronic"},
  FOLK_SINGER_SONGWRITER: {
    name: "MUSIC",
    value: "folk_singer_songwriter",
  },
  HIP_HOP_RAP: {name: "MUSIC", value: "hip_hop_rap"},
  HOUSE: {name: "MUSIC", value: "house"},
  INDIE: {name: "MUSIC", value: "indie"},
  JAZZ_BLUES: {name: "MUSIC", value: "jazz_blues"},
  LATIN: {name: "MUSIC", value: "latin"},
  METAL: {name: "MUSIC", value: "metal"},
  PIANO: {name: "MUSIC", value: "piano"},
  POP: {name: "MUSIC", value: "pop"},
  R_B_SOUL: {name: "MUSIC", value: "r_b_soul"},
  REGGAE: {name: "MUSIC", value: "reggae"},
  REGGAETON: {name: "MUSIC", value: "reggaeton"},
  ROCK: {name: "MUSIC", value: "rock"},
  SOUNDTRACK: {name: "MUSIC", value: "soundtrack"},
  TECHNO: {name: "MUSIC", value: "techno"},
  TRANCE: {name: "MUSIC", value: "trance"},
  TRAP: {name: "MUSIC", value: "trap"},
  TRIPHOP: {name: "MUSIC", value: "triphop"},
  WORLD: {name: "MUSIC", value: "world"},
};
const SubAudioTypes = {
  AUDIOBOOKS: {name: "AUDIO", value: "audiobooks"},
  BUSINESS: {name: "AUDIO", value: "business"},
  COMEDY: {name: "AUDIO", value: "comedy"},
  ENTERTAINMENT: {name: "AUDIO", value: "entertainment"},
  LEARNING: {name: "AUDIO", value: "learning"},
  NEWS_POLITICS: {name: "AUDIO", value: "news_politics"},
  RELIGION_SPIRITUALITY: {type: "religion_spirituality"},
  SCIENCE: {name: "AUDIO", value: "science"},
  SPORTS: {name: "AUDIO", value: "sports"},
  STORYTELLING: {name: "AUDIO", value: "storytelling"},
  TECHNOLOGY: {name: "AUDIO", value: "technology"},
};
export const GenreTypes = {
  NONE: {name: "NONE", value: "None", sub: false},
  CUSTOM: {name: "CUSTOM", value: "Custom",sub: false},
  MUSIC: {
    name: "MUSIC",
    value: "Music",
    sub: true,
    subTypes: Object.values(SubMusicTypes)
  },
  AUDIO: {
    name: "AUDIO",
    value: "Audio",
    sub: true,
    subTypes: Object.values(SubAudioTypes)
  },
};