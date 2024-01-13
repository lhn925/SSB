package sky.Sss.domain.track.model;

public enum TrackMinimumPlayTime {
    MINI_NUM_SECOND(60);

    private int seconds;

    TrackMinimumPlayTime(int seconds) {
        this.seconds = seconds;
    }

    public int getSeconds() {
        return seconds;
    }


}
