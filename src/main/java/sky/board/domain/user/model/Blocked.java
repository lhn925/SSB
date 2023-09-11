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

    public static boolean BLOCKED() {
        return BLOCKED.getValue();
    }

    public static boolean UNBLOCKED() {
        return UNBLOCKED.getValue();
    }

    public static Blocked valueOf(Boolean blocked) {
        if (blocked) {
            return Blocked.BLOCKED;
        } else {
            return Blocked.UNBLOCKED;
        }
    }
}
