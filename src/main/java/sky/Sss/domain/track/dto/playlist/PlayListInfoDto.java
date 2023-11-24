package sky.Sss.domain.track.dto.playlist;


import lombok.Getter;
import lombok.Setter;
import sky.Sss.domain.track.entity.playList.SsbPlayListSettings;

@Setter
@Getter
public class PlayListInfoDto {

    private Long id;
    private String token;
    private String title;
    private String coverUrl;
    private Boolean isDownload;
    private String userName;

    public PlayListInfoDto(SsbPlayListSettings ssbPlayListSettings) {
        this.id = ssbPlayListSettings.getId();
        this.token = ssbPlayListSettings.getToken();
        this.title = ssbPlayListSettings.getTitle();
        this.coverUrl = ssbPlayListSettings.getCoverUrl();
        this.isDownload = ssbPlayListSettings.getIsDownload();
        this.userName = ssbPlayListSettings.getUser().getUserName();
    }
}
