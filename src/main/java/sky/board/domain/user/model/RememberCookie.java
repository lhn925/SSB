package sky.board.domain.user.model;

public enum RememberCookie {

    NAME("rememberMe");

    private String value;

    RememberCookie(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
