package sky.Sss.domain.track.model;

public enum SubAudioGenre {
    AUDIOBOOKS("audiobooks",1),
    BUSINESS("business",2),
    COMEDY("comedy",3),
    ENTERTAINMENT("entertainment",4),
    LEARNING("learning",5),
    NEWS_POLITICS("news_politics",6),
    RELIGION_SPIRITUALITY("religion_spirituality",7),
    SCIENCE("science",8),
    SPORTS("sports",9),
    STORYTELLING("storytelling",10),
    TECHNOLOGY("technology",11);

    private final String value;
    private final Integer index;

    SubAudioGenre(String value, Integer index) {
        this.value = value;
        this.index = index;
    }
    public static SubAudioGenre findBySubGenre(String genre) {
        SubAudioGenre[] values = values();
        for (SubAudioGenre value : values) {
            if (value.name().equals(genre)) {
                return value;
            }
        }
        return null;
    }
    public String getValue() {
        return value;
    }
    public Integer getIndex() {
        return index;
    }


}
