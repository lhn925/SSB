package sky.Sss.domain.track.dto.playlist.reply;


import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.dto.track.redis.RedisBaseReplyDto;
import sky.Sss.domain.track.entity.playList.reply.SsbPlyReply;
import sky.Sss.domain.track.entity.track.reply.SsbTrackReply;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
public class RedisPlyReplyDto extends RedisBaseReplyDto {
    public RedisPlyReplyDto(SsbPlyReply ssbPlyReply) {
        super(ssbPlyReply.getId()
            ,ssbPlyReply.getToken()
            ,ssbPlyReply.getUser().getId()
            ,ssbPlyReply.getSsbPlayListSettings().getId()
            ,ssbPlyReply.getContents()
            ,ssbPlyReply.getParentId()
            ,ssbPlyReply.getReplyOrder()
            ,ssbPlyReply.getCreatedDateTime());
    }
}
