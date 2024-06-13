package sky.Sss.domain.track.dto.common.repost;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sky.Sss.domain.track.entity.SsbRepost;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RepostInfoDto {
    // 해당 트랙이나 플레이리스트에 아이디
    private String targetToken;
    private Long targetId;
    private SsbRepost ssbRepost;

    public RepostInfoDto(String targetToken, SsbRepost ssbRepost) {
        this.targetToken = targetToken;
        this.ssbRepost = ssbRepost;
    }

    public RepostInfoDto(String targetToken, Long targetId, SsbRepost ssbRepost) {
        this.targetToken = targetToken;
        this.targetId = targetId;
        this.ssbRepost = ssbRepost;
    }
}
