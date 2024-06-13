package sky.Sss.domain.track.dto.track.reply;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.dto.track.redis.BaseRedisReplyDto;
import sky.Sss.domain.track.entity.track.reply.SsbTrackReply;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
public class TrackRedisReplyDto extends BaseRedisReplyDto {

    // 댓글 아이디
    // 댓글 토큰
    private int timeLine;

    public TrackRedisReplyDto(SsbTrackReply ssbTrackReply) {
        super(ssbTrackReply.getId()
            ,ssbTrackReply.getToken()
            ,ssbTrackReply.getUser().getId()
            ,ssbTrackReply.getSsbTrack().getId()
            ,ssbTrackReply.getContents()
            ,ssbTrackReply.getParentId()
            ,ssbTrackReply.getReplyOrder()
            ,ssbTrackReply.getCreatedDateTime(),ssbTrackReply.getSsbTrack().getToken());
        this.timeLine = ssbTrackReply.getTimeLine();
    }
}
