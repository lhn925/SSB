package sky.Sss.domain.user.model;

import org.bytedeco.javacpp.opencv_core.RefOrVoid.type;

/**
 * push message 에 구분 하는 enum
 */
public enum ContentsType {
    TRACK("Track","/tracks/info/"),PLAYLIST("PlayList","/tracks/ply/info/"),REPLY("Reply",""),
    USER("User", "/users/info/");

    private final String type;
    private final String url;

    ContentsType(String type,String url) {
        this.type = type;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }
}
