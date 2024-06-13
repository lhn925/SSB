package sky.Sss.domain.track.dto.playlist.req;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import sky.Sss.domain.track.dto.track.common.BasePlayListDto;

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


    // order update 를 했는지 여부 확인
    private Boolean isOrder;


    private List<PlayListTrackUpdateDto> trackUpdateDtoList;
    private List<PlayListTrackDeleteDto> trackDeleteDtoList;

}
