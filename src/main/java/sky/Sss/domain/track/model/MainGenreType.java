package sky.Sss.domain.track.model;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum MainGenreType {
    CUSTOM("CUSTOM", 1), NONE("NONE", 2), AUDIO("AUDIO", 3), MUSIC("MUSIC", 4);

    private final String value;
    private final Integer index;

    MainGenreType(String value, Integer index) {
        this.value = value;
        this.index = index;
    }
    public String getValue() {
        return value;
    }
    public Integer getIndex() {
        return index;
    }
    // main 카테고리 목록 검색
    public static MainGenreType findByType(String type) {
        MainGenreType[] values = values();
        for (MainGenreType value : values) {
            if (value.getValue().equals(type)) {
                return value;
            }
        }
        return MainGenreType.NONE;
    }
    public static MainGenreType findByIndex(Integer index) {
        MainGenreType[] values = values();
        for (MainGenreType value : values) {
            if (value.getIndex() == index) {
                return value;
            }
        }
        return MainGenreType.NONE;
    }

    // 카테고리 하위 목록 value 값 반환
    public String getSubGenreValue(String subGenre) {
        switch (this) {
            case AUDIO:
                return SubAudioGenre.findBySubGenre(subGenre).getValue();
            case MUSIC:
                return SubMusicGenre.findBySubGenre(subGenre).getValue();
            case CUSTOM:
                return subGenre;
            default:
                return null;
        }
    }

    // 카테고리 하위 목록 inex 값 반환
    public Integer getSubGenreIndex(String subGenre) {
        switch (this) {
            case AUDIO:
                return SubAudioGenre.findBySubGenre(subGenre).getIndex();
            case MUSIC:
                return SubMusicGenre.findBySubGenre(subGenre).getIndex();
            case CUSTOM:
                return 0;
            default:
                return null;
        }
    }

}
