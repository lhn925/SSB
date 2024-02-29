package sky.Sss.domain.track.dto.playlist.reply;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.entity.playList.reply.SsbPlyReply;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
public class PlyReplyCacheDto {

    private long id;
    private String userToken;
    private String contents;
    private long parentId;
    private int replyOrder;
    private boolean isStatus;

    public PlyReplyCacheDto(SsbPlyReply ssbPlyReply) {
        this.setId(ssbPlyReply.getId());
        this.setUserToken(ssbPlyReply.getUser().getToken());
        this.setContents(ssbPlyReply.getContents());
        this.setReplyOrder(ssbPlyReply.getReplyOrder());
        this.setParentId(ssbPlyReply.getParentId());
    }
}
