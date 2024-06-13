package sky.Sss.domain.track.dto.common.repost;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.entity.SsbRepost;
import sky.Sss.domain.user.model.ContentsType;


@Getter
@NoArgsConstructor
@Setter(AccessLevel.PRIVATE)
public class RepostRedisDto {

    private Long id;
    private String token;
    private Long contentsId;
    private String targetToken;
    private Long uid;
    private String comment;
    private ContentsType contentsType;
    private LocalDateTime createdDateTime;

    public RepostRedisDto(SsbRepost ssbRepost,String targetToken) {
        this.id = ssbRepost.getId();
        this.token = ssbRepost.getToken();
        this.contentsId = ssbRepost.getContentsId();
        this.uid = ssbRepost.getUser().getId();
        this.comment = ssbRepost.getComment();
        this.contentsType = ssbRepost.getContentsType();
        this.createdDateTime = ssbRepost.getCreatedDateTime();
        this.targetToken = targetToken;
    }
}
