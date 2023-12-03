package sky.Sss.domain.track.model;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum MusicGenre {
    ALTERNATIVE_ROCK("alternative_rock",1),
    AMBIENT("ambient",2),
    CLASSICAL("classical",3),
    COUNTRY("country",4),
    DANCE_EDM("dance_edm",5),
    DANCEHALL("dancehall",6),
    DEEP_HOUSE("deep_house",7),
    DISCO("disco",8),
    DRUM_BASS("drum_bass",9),
    DUBSTEP("dubstep",10),
    ELECTRONIC("electronic",11),
    FOLK_SINGER_SONGWRITER("folk_singer_songwriter",12),
    HIP_HOP_RAP("hip_hop_rap",13),
    HOUSE("house",14),
    INDIE("indie",15),
    JAZZ_BLUES("jazz_blues",16),
    LATIN("latin",17),
    METAL("metal",18),
    PIANO("piano",19),
    POP("pop",20),
    R_B_SOUL("r_b_soul",21),
    REGGAE("reggae",22),
    REGGAETON("reggaeton",23),
    ROCK("rock",24),
    SOUNDTRACK("soundtrack",25),
    TECHNO("techno",26),
    TRANCE("trance",27),
    TRAP("trap",28),
    TRIPHOP("triphop",29),
    WORLD("world",30);

    private final String value;
    private final Integer index;

    MusicGenre(String value, Integer index) {
        this.value = value;
        this.index = index;
    }

    public static MusicGenre findBySubGenre(String arg) {
        MusicGenre[] values = values();
        for (MusicGenre value : values) {
            if (value.getValue().equals(arg)) {
                return value;
            }
        }
        return null;
    }
    public String getValue() {
        return value;
    }

    public Integer getIndex() {
        return index;
    }
}
