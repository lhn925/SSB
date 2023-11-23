package sky.Sss.domain.track.dto.temp;


import lombok.Getter;
import lombok.Setter;
import sky.Sss.global.file.dto.UploadTrackFileDto;

@Getter
@Setter
public class TempTrackInfoDto {
    private Long id;
    private String token;
    private UploadTrackFileDto uploadTrackFile;

    public TempTrackInfoDto(Long id, String token, UploadTrackFileDto uploadTrackFileDto) {
        this.id = id;
        this.token = token;
        this.uploadTrackFile = uploadTrackFileDto;
    }
}
