package sky.board.domain.user.model;

/**
 * 사용자의 탈퇴 여부를 나타내는 enum
 */
public enum Blocked {
    BLOCKED(true), UNBLOCKED(false);

    private Boolean enabled;

    Blocked(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getValue() {
        return this.enabled;
    }

    public static boolean ENABLED() {
        return BLOCKED.getValue();
    }

    public static boolean UNABlED() {
        return UNBLOCKED.getValue();
    }

    public static Blocked valueOf(Boolean enabled) {
        if (enabled) {
            return Blocked.BLOCKED;
        } else {
            return Blocked.UNBLOCKED;
        }
    }
}
