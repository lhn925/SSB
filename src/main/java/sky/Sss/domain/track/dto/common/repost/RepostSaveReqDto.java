package sky.Sss.domain.track.dto.common.repost;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import sky.Sss.domain.user.model.ContentsType;

@Getter
public class RepostSaveReqDto {
    // 해당 트랙이나 플레이리스트에 아이디
    @Min(value = 1)
    private Long targetId;

    // 해당 트랙이나 플레이리스트에 토큰
    @NotBlank
    private String targetToken;

    private ContentsType contentsType;
}
