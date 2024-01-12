package sky.Sss.domain.track.model;

public enum TrackMiniNumPlayTime {
    MINI_NUM_SECOND(60);
    
    private final int seconds;

    TrackMinimumPlayTime(String description, int seconds) {
        this.description = description;
        this.seconds = seconds;
    }

    public String getDescription() {
        return description;
    }

    public int getSeconds() {
        return seconds;
    }


}
