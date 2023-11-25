package sky.Sss.domain.track.model;

public enum AudioGenre {
    AUDIOBOOKS("audiobooks"),
    BUSINESS("business"),
    COMEDY("comedy"),
    ENTERTAINMENT("entertainment"),
    LEARNING("learning"),
    NEWS_POLITICS("news_politics"),
    RELIGION_SPIRITUALITY("religion_spirituality"),
    SCIENCE("science"),
    SPORTS("sports"),
    STORYTELLING("storytelling"),
    TECHNOLOGY("technology");

    private final String value;

    AudioGenre(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
