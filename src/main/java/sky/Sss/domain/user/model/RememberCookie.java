package sky.Sss.domain.user.model;

public enum RememberCookie {

    KEY("rememberMe");

    private final String value;

    RememberCookie(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
