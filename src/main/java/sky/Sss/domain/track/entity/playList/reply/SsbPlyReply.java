package sky.Sss.domain.track.entity.playList.reply;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import sky.Sss.domain.track.dto.playlist.reply.PlyReplySaveReqDto;
import sky.Sss.domain.track.dto.track.reply.TrackReplySaveReqDto;
import sky.Sss.domain.track.entity.playList.SsbPlayListSettings;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.Status;
import sky.Sss.global.base.BaseTimeEntity;
import sky.Sss.global.utili.JsEscape;


@Slf4j
@Getter
@Setter(value = AccessLevel.PRIVATE)
@Entity
@NoArgsConstructor(access = PROTECTED)
public class SsbPlyReply extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    // 유저 정보
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "uid", nullable = false)
    private User user;

    @Column(nullable = false,unique = true)
    private String token;

    // 트랙 정보
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "settings_id", nullable = false)
    private SsbPlayListSettings ssbPlayListSettings;

    // 내용
    @Column(nullable = false)
    private String contents;

    // 대댓글일 경우 댓글 id
    @Column(name = "parent_id")
    private Long parentId;

    // 대댓글 순서
    private Integer replyOrder;

    public static SsbPlyReply create(PlyReplySaveReqDto trackReplySaveReqDto, User user, SsbPlayListSettings ssbPlayListSettings) {
        SsbPlyReply ssbPlyReply = new SsbPlyReply();
        ssbPlyReply.setUser(user);
        ssbPlyReply.setSsbPlayListSettings(ssbPlayListSettings);
        ssbPlyReply.setContents(JsEscape.escapeJS(trackReplySaveReqDto.getContents()));
        ssbPlyReply.setParentId(trackReplySaveReqDto.getParentId());
        return ssbPlyReply;
    }

    public static void updateReplyOrder(SsbPlyReply ssbPlyReply, int replyOrder) {
        ssbPlyReply.setReplyOrder(replyOrder);
    }
    public static void updateParentId(SsbPlyReply ssbPlyReply, long parentId) {
        ssbPlyReply.setParentId(parentId);
    }
    public static void updateToken(SsbPlyReply ssbPlyReply, String token) {
        ssbPlyReply.setToken(token);
    }
}
