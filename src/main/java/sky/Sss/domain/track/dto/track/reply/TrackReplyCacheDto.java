package sky.Sss.domain.track.dto.track.reply;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.entity.track.reply.SsbTrackReply;
import sky.Sss.domain.track.service.track.TrackActionService;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
public class TrackReplyCacheDto {

    private long id;
    private String userToken;
    private String contents;
    private int timeLine;
    private long parentId;
    private int replyOrder;

    public TrackReplyCacheDto(SsbTrackReply ssbTrackReply) {
        this.setId(ssbTrackReply.getId());
        this.setUserToken(ssbTrackReply.getUser().getToken());
        this.setContents(ssbTrackReply.getContents());
        this.setReplyOrder(ssbTrackReply.getReplyOrder());
        this.setParentId(ssbTrackReply.getParentId());
        this.setTimeLine(ssbTrackReply.getTimeLine());
    }
}
