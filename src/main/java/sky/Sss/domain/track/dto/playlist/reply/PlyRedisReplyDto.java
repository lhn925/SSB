package sky.Sss.domain.track.dto.playlist.reply;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.dto.common.reply.BaseRedisReplyDto;
import sky.Sss.domain.track.entity.playList.reply.SsbPlyReply;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
public class PlyRedisReplyDto extends BaseRedisReplyDto {

    public PlyRedisReplyDto(SsbPlyReply ssbPlyReply) {
        super(ssbPlyReply.getId()
            , ssbPlyReply.getToken()
            , ssbPlyReply.getUser().getId()
            , ssbPlyReply.getSsbPlayListSettings().getId()
            , ssbPlyReply.getContents()
            , ssbPlyReply.getParentId()
            , ssbPlyReply.getReplyOrder()
            , ssbPlyReply.getCreatedDateTime(), ssbPlyReply.getToken());
    }
}
