package sky.Sss.domain.track.model;

public enum PlayBackStatus {
    //완료
    COMPLETED("COMPLETED"),
    //알수없음
    INCOMPLETE("INCOMPLETE");

    private String value;
    PlayBackStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
