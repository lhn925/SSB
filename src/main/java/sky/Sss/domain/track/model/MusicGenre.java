package sky.Sss.domain.track.model;

public enum MusicGenre {
    ALTERNATIVE_ROCK("alternative_rock"),
    AMBIENT("ambient"),
    CLASSICAL("classical"),
    COUNTRY("country"),
    DANCE_EDM("dance_edm"),
    DANCEHALL("dancehall"),
    DEEP_HOUSE("deep_house"),
    DISCO("disco"),
    DRUM_BASS("drum_bass"),
    DUBSTEP("dubstep"),
    ELECTRONIC("electronic"),
    FOLK_SINGER_SONGWRITER("folk_singer_songwriter"),
    HIP_HOP_RAP("hip_hop_rap"),
    HOUSE("house"),
    INDIE("indie"),
    JAZZ_BLUES("jazz_blues"),
    LATIN("latin"),
    METAL("metal"),
    PIANO("piano"),
    POP("pop"),
    R_B_SOUL("r_b_soul"),
    REGGAE("reggae"),
    REGGAETON("reggaeton"),
    ROCK("rock"),
    SOUNDTRACK("soundtrack"),
    TECHNO("techno"),
    TRANCE("trance"),
    TRAP("trap"),
    TRIPHOP("triphop"),
    WORLD("world");

    private final String value;

    MusicGenre(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
