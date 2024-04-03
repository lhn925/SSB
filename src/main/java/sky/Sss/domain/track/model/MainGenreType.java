package sky.Sss.domain.track.model;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum MainGenreType {
    CUSTOM("Custom", 1), NONE("None", 2),
    MUSIC("Music", 3), AUDIO("Audio", 4);

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
            if (value.name().equals(type)) {
                return value;
            }
        }
        return MainGenreType.NONE;
    }
    public static MainGenreType findByIndex(Integer index) {
        MainGenreType[] values = values();
        for (MainGenreType value : values) {
            if (Objects.equals(value.getIndex(), index)) {
                return value;
            }
        }
        return MainGenreType.NONE;
    }

    // 카테고리 하위 목록 value 값 반환
    public String getSubGenreValue(String subGenre) {
        return switch (this) {
            case AUDIO -> Objects.requireNonNull(SubAudioGenre.findBySubGenre(subGenre)).getValue();
            case MUSIC -> Objects.requireNonNull(SubMusicGenre.findBySubGenre(subGenre)).getValue();
            case CUSTOM -> subGenre;
            default -> null;
        };
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
