package sky.Sss.domain.track.dto.track.reply;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.dto.track.redis.RedisBaseReplyDto;
import sky.Sss.domain.track.entity.track.reply.SsbTrackReply;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
public class RedisTrackReplyDto extends RedisBaseReplyDto {

    // 댓글 아이디
    // 댓글 토큰
    private int timeLine;

    public RedisTrackReplyDto(SsbTrackReply ssbTrackReply) {
        super(ssbTrackReply.getId()
            ,ssbTrackReply.getToken()
            ,ssbTrackReply.getUser().getId()
            ,ssbTrackReply.getSsbTrack().getId()
            ,ssbTrackReply.getContents()
            ,ssbTrackReply.getParentId()
            ,ssbTrackReply.getReplyOrder()
            ,ssbTrackReply.getCreatedDateTime());
        this.timeLine = ssbTrackReply.getTimeLine();
    }
}
