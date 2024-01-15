package sky.Sss.domain.track.model;

public enum PlayStatus {
    //완료
    COMPLETED("COMPLETED"),

    //알수없음
    INCOMPLETE("INCOMPLETE");

    private String status;
    PlayStatus(String status) {
        this.status = status;
    }

    public static PlayStatus getPlayStatus (Boolean isStatus) {
        if (isStatus) {
            return COMPLETED;
        } else {
            return INCOMPLETE;
        }
    }


    public String getStatus() {
        return this.status;
    }
}
