package sky.Sss.domain.track.entity.track.reply;


import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import sky.Sss.domain.track.dto.track.reply.TrackRedisReplyDto;
import sky.Sss.domain.track.dto.track.reply.TrackReplySaveReqDto;
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
public class SsbTrackReply extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 유저 정보
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "uid", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String token;

    // 트랙 정보
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "track_id", nullable = false)
    private SsbTrack ssbTrack;

    // 타임 라인 정보
    @Column(nullable = false)
    private Integer timeLine;

    // 내용
    @Column(nullable = false)
    private String contents;

    // 대댓글일 경우 댓글 id
    @Column(name = "parent_id")
    private Long parentId;

    // 대댓글 순서
    private Integer replyOrder;


    public static SsbTrackReply create(TrackReplySaveReqDto trackReplySaveReqDto, User user, SsbTrack ssbTrack) {
        SsbTrackReply ssbTrackReply = new SsbTrackReply();
        ssbTrackReply.setUser(user);
        ssbTrackReply.setSsbTrack(ssbTrack);

        int timeLine = trackReplySaveReqDto.getTimeLine();

        // timeLine 이 트랙 길이 보다 길면은 0 으로 초기화
        if (timeLine > ssbTrack.getTrackLength()) {
            timeLine = 0;
        }
        ssbTrackReply.setTimeLine(timeLine);
        ssbTrackReply.setContents(JsEscape.escapeJS(trackReplySaveReqDto.getContents()));
        ssbTrackReply.setParentId(trackReplySaveReqDto.getParentId());
        return ssbTrackReply;
    }

    public static SsbTrackReply redisDtoToSsbTrackReply(TrackRedisReplyDto redisReplyDto, SsbTrack ssbTrack,
        User user) {
        SsbTrackReply ssbTrackReply = new SsbTrackReply();
        ssbTrackReply.setUser(user);
        ssbTrackReply.setSsbTrack(ssbTrack);
        ssbTrackReply.setId(redisReplyDto.getId());
        ssbTrackReply.setReplyOrder(redisReplyDto.getReplyOrder());
        ssbTrackReply.setParentId(redisReplyDto.getParentId());
        ssbTrackReply.setContents(redisReplyDto.getContents());
        ssbTrackReply.setTimeLine(redisReplyDto.getTimeLine());
        ssbTrackReply.setCreatedDateTime(redisReplyDto.getCreatedDateTime());
        return ssbTrackReply;
    }

    public static void updateReplyOrder(SsbTrackReply ssbTrackReply, int replyOrder) {
        ssbTrackReply.setReplyOrder(replyOrder);
    }

    public static void updateParentId(SsbTrackReply ssbTrackReply, long parentId) {
        ssbTrackReply.setParentId(parentId);
    }

    public static void updateToken(SsbTrackReply ssbTrackReply, String token) {
        ssbTrackReply.setToken(token);
    }

    @Builder
    public SsbTrackReply(Long id) {
        this.id = id;
    }
}
