package sky.Sss.domain.track.dto.common.repost;


import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.user.model.ContentsType;

@Getter
@Setter(PRIVATE)
@NoArgsConstructor(access = PROTECTED)
public class RepostRmReqDto {
    // 해당 트랙이나 플레이리스트에 아이디
    @NotBlank
    private String repostToken;
    @Min(value = 1)
    private Long repostId;
    private ContentsType contentsType;

}
