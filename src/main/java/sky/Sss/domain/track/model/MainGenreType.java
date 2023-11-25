package sky.Sss.domain.track.model;

public enum MainGenreType {
    CUSTOM("CUSTOM"), NONE("NONE"), AUDIO("AUDIO"), MUSIC("MUSIC");

    private final String genre;
    MainGenreType(String value) {
        this.genre = value;
    }

    public String getSubGenreType(String subGenre) {
        switch (this) {
            case AUDIO :
                return AudioGenre.valueOf(subGenre).getValue();
            case MUSIC :
                return MusicGenre.valueOf(subGenre).getValue();
            case CUSTOM:
                return subGenre;
            default:
                return null;
        }
    }

}
