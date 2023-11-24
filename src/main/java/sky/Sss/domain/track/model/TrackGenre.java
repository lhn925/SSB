package sky.Sss.domain.track.model;

public enum TrackGenre {
    CUSTOM("CUSTOM"), NONE("NONE"), AUDIO("AUDIO"), MUSIC("MUSIC");

    private String genre;
    TrackGenre(String value) {
        this.genre = value;
    }
}
