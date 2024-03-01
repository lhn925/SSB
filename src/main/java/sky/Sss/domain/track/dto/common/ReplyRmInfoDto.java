package sky.Sss.domain.track.dto.common;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * track or PlayList 에 정보를 담는 DTO
 */
@Getter
@Setter(value = AccessLevel.PRIVATE)
public class ReplyRmInfoDto {

    private Long replyId;
    private String replyToken;
    private Long replyOwner;
    private Long targetOwner;
    private String targetToken;

    public ReplyRmInfoDto(Long replyId, String replyToken, Long replyOwner, Long trackOwner, String trackToken) {
        this.replyId = replyId;
        this.replyToken = replyToken;
        this.replyOwner = replyOwner;
        this.targetOwner = trackOwner;
        this.targetToken = trackToken;
    }
}
