package sky.Sss.domain.track.dto.common;


import static lombok.AccessLevel.PROTECTED;

import lombok.Getter;
import lombok.NoArgsConstructor;
import sky.Sss.domain.user.entity.User;

@NoArgsConstructor(access = PROTECTED)
@Getter
public class TargetInfoDto {

    // reply,track,playList 의 고유 아이디
    private Long targetId;

    // reply like 일 경우 trackId or playList Id
    private Long parentId;
    // reply,track,playList 의 고유 토큰
    private String targetToken;
    private String targetContents;
    private User toUser;
    private Boolean isPrivacy;

    public TargetInfoDto(Long targetId, String targetToken, String targetContents, User fromUser,Boolean isPrivacy) {
        this.targetId = targetId;
        this.targetToken = targetToken;
        this.targetContents = targetContents;
        this.toUser = fromUser;
        this.parentId = 0L;
        this.isPrivacy = isPrivacy;
    }
    public TargetInfoDto(Long targetId, String targetToken, String targetContents, User fromUser,Long parentId) {
        this.targetId = targetId;
        this.targetToken = targetToken;
        this.targetContents = targetContents;
        this.toUser = fromUser;
        this.parentId = parentId;
    }

    public TargetInfoDto(Long targetId, Long parentId, String targetToken, String targetContents, User toUser,
        Boolean isPrivacy) {
        this.targetId = targetId;
        this.parentId = parentId;
        this.targetToken = targetToken;
        this.targetContents = targetContents;
        this.toUser = toUser;
        this.isPrivacy = isPrivacy;
    }
}
