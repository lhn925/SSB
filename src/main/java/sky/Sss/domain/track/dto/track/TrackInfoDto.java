package sky.Sss.domain.track.dto.track;


import lombok.Getter;
import lombok.Setter;
import sky.Sss.domain.track.entity.track.SsbTrack;

@Setter
@Getter
public class TrackInfoDto {

    private String title;
    private String coverUrl;
    private Boolean isDownload;
    private String userName;
    private Long id;
    private String token;

    public TrackInfoDto(SsbTrack ssbTrack,String userName) {
        this.title = ssbTrack.getTitle();
        this.coverUrl = ssbTrack.getCoverUrl();
        this.isDownload = ssbTrack.getIsDownload();
        this.userName = userName;
        this.id = ssbTrack.getId();
        this.token = ssbTrack.getToken();
    }
}
