package sky.Sss.domain.track.model;

public enum PlayListType {
    PLAYLIST("PLAYLIST"), ALBUM("ALBUM"), EP("EP"), SINGLE("SINGLE"), COMPILATION("COMPILATION");

    private String type;
    PlayListType(String type) {
        this.type = type;
    }

}
