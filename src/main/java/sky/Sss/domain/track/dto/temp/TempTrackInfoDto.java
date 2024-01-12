package sky.Sss.domain.track.dto.temp;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.global.file.dto.UploadTrackFileDto;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TempTrackInfoDto {
    private Long id;
    private String token;
    private UploadTrackFileDto uploadTrackFile;
    public static TempTrackInfoDto create(Long id, String token, UploadTrackFileDto uploadTrackFileDto) {
        TempTrackInfoDto tempTrackInfoDto = new TempTrackInfoDto();
        tempTrackInfoDto.setId(id);
        tempTrackInfoDto.setToken(token);
        tempTrackInfoDto.setUploadTrackFile(uploadTrackFileDto);
        return tempTrackInfoDto;
    }
}
