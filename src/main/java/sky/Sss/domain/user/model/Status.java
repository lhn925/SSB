package sky.Sss.domain.user.model;

/**
 * 상태값을 나타내는 상수
 */
public enum Status {
    ON(true), OFF(false);

    private final Boolean isStatus;

    Status(Boolean isStatus) {
        this.isStatus = isStatus;
    }

    public Boolean getValue() {
        return this.isStatus;
    }
}
