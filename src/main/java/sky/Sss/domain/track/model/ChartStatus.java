package sky.Sss.domain.track.model;

public enum ChartStatus {
    // 차트에 반영되지 않음
    NOT_REFLECTED("NOT_REFLECTED"),

    // 차트에 반영됨
    REFLECTED("REFLECTED");


    private String status;
    ChartStatus(String playBackStatus) {
        this.status = playBackStatus;
    }
}
