package sky.Sss.domain.track.dto.track.redis;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
public class BaseRedisReplyDto {
    // 댓글 아이디
    private long id;
    // 댓글 토큰
    private String token;
    private Long uid;
    private Long targetId;
    private String targetToken;
    private String contents;
    private long parentId;
    private int replyOrder;
    private LocalDateTime createdDateTime;


    public BaseRedisReplyDto(long id, String token, Long uid, Long targetId, String contents, long parentId,
        int replyOrder,
        LocalDateTime createdDateTime,String targetToken) {
        this.id = id;
        this.token = token;
        this.uid = uid;
        this.targetId = targetId;
        this.contents = contents;
        this.parentId = parentId;
        this.replyOrder = replyOrder;
        this.createdDateTime = createdDateTime;
        this.targetToken = targetToken;
    }
}
