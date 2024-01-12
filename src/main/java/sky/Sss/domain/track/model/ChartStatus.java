package sky.Sss.domain.track.model;

public enum PlayBackStatus {
    //재생중
    PLAYING("PLAYING"),
    //정지
    PAUSED("PAUSED"),
    //완료
    COMPLETED("COMPLETED"),
    //알수없음
    INCOMPLETE("INCOMPLETE");

    private String status;
    PlayBackStatus(String playBackStatus) {
        this.status = playBackStatus;
    }
}
