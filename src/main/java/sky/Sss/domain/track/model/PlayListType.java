package sky.Sss.domain.track.model;


public enum PlayListType {
    PLAYLIST("Playlist",1), ALBUM("Album",2), EP("EP",3), SINGLE("Single",4), COMPILATION("Compilation",5);

    private final String type;
    private final Integer index;
    PlayListType(String type,Integer index) {
        this.type = type;
        this.index = index;
    }

    public static PlayListType findByListType(String type) {
        PlayListType[] values = values();
        for (PlayListType value : values) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return PLAYLIST;
    }

    public String getType() {
        return type;
    }

    public Integer getIndex() {
        return index;
    }
}
