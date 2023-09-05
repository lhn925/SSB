package sky.board.domain.user.model;

/**
 * 사용자의 탈퇴 여부를 나타내는 enum
 */
public enum Enabled {
    ENABLED(true),UNABlED(false);

    private Boolean enabled;

    Enabled(Boolean enabled) {
        this.enabled = enabled;
    }
    public Boolean getValue() {
        return this.enabled;
    }
    public static boolean ENABLED() {
        return ENABLED.getValue();
    }
    public static boolean UNABlED() {
        return UNABlED.getValue();
    }
    public static Enabled valueOf(Boolean enabled) {
        if (enabled) {
            return Enabled.ENABLED;
        } else {
            return Enabled.UNABlED;
        }
    }
}
