package sky.Sss.domain.track.model;

public enum PlayStatus {
    //완료
    COMPLETED("COMPLETED"),

    //알수없음
    INCOMPLETE("INCOMPLETE");

    private String value;
    PlayStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
