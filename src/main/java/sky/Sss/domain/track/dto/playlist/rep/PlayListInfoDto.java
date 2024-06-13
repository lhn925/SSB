package sky.Sss.domain.track.dto.playlist.rep;


import static lombok.AccessLevel.PRIVATE;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.entity.playList.SsbPlayListSettings;

@Setter(PRIVATE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlayListInfoDto {

    private Long id;
    private String token;
    private String title;
    private String coverUrl;
    private Boolean isDownload;
    private String userName;
    private LocalDateTime createdDateTime;


    public static PlayListInfoDto create(SsbPlayListSettings ssbPlayListSettings) {
        PlayListInfoDto playListInfoDto = new PlayListInfoDto();
        playListInfoDto.setId(ssbPlayListSettings.getId());

        playListInfoDto.setToken(ssbPlayListSettings.getToken());
        playListInfoDto.setTitle(ssbPlayListSettings.getTitle());
        playListInfoDto.setCoverUrl(ssbPlayListSettings.getCoverUrl());
        playListInfoDto.setIsDownload(ssbPlayListSettings.getIsDownload());
        playListInfoDto.setUserName(ssbPlayListSettings.getUser().getUserName());
        playListInfoDto.setCreatedDateTime(ssbPlayListSettings.getCreatedDateTime());

        return playListInfoDto;
    }


}