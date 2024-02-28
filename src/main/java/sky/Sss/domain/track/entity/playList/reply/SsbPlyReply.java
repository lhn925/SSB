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
import sky.Sss.domain.track.entity.playList.SsbPlayListSettings;
import sky.Sss.domain.user.entity.User;
import sky.Sss.global.base.BaseTimeEntity;

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
    @JoinColumn(name = "uid",nullable = false)
    private User user;

    // 트랙 정보
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "settings_id",nullable = false)
    private SsbPlayListSettings ssbPlayListSettings;

    // 내용
    @Column(nullable = false)
    private String contents;

    // 태그 (다중 태그) 유저 고유 Index 저장
    private String hashTags;

    // 대댓글일 경우 댓글 id
    @Column(name = "parent_id")
    private Long parentId;

    // 대댓글 순서
    private Integer replyOrder;

    // 삭제 유무
    @Column(nullable = false)
    private Boolean isStatus;

}
