package sky.board.domain.user.model;

public enum RememberCookie {

    KEY("rememberMe");

    private String value;

    RememberCookie(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
