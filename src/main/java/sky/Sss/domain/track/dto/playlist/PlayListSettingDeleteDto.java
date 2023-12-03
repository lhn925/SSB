package sky.Sss.domain.track.dto.playlist;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import sky.Sss.domain.track.dto.BaseTrackDto;
import sky.Sss.domain.track.dto.track.BasePlayListDto;

@Getter
@Setter
public class PlayListSettingUpdateDto extends BasePlayListDto {
    // 플레이리스트 타입
    // 플레이리스트 수록곡 order 수정
    // 플레이리스트 수록곡 삭제 목록
    @NotNull
    private Long id;
    @NotBlank
    private String token;
    @NotBlank
    private String playListType;
    private List<PlayListTrackUpdateDto> playListTrackUpdateDtoList;
    private List<PlayListTrackDeleteDto> playListTrackDeleteDtoList;

}
