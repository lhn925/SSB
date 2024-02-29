package sky.Sss.domain.user.entity;


import static jakarta.persistence.EnumType.STRING;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import sky.Sss.domain.user.model.ContentsType;
import sky.Sss.domain.user.model.PushMsgType;
import sky.Sss.global.base.BaseTimeEntity;


@Slf4j
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class UserPushMessages extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    // 수신 유저
    @JoinColumn(name = "to_uid", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User toUser;

    // 발신 유저
    @JoinColumn(name = "from_uid", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User fromUser;

    // pushType
    // like: 트랙 좋아요 (트랙 정보 반환),앨범 좋아요 (앨범 정보 반환), 댓글 좋아요
    // 팔로우: 팔로우 -> fromUser 정보 반환
    // 댓글 : 대댓글,대댓글 (해당 댓글 정보 위치 반환)
    /**
     * 댓글(트랙,플레이리스트)링크 이동
     * 팔로우나 좋아요 한사람의 프로필 이동
     * 플레이리스트 이동
     * <p>
     * 떙땡님 외 16명이 무슨무슨 에 좋아요를 눌렀습니다.
     * <p>
     * 트랙이나 플레이리스트에 댓글을 달았을 경우
     * https://soundcloud.com/사용자아이디/노래제목/comment-댓글Index
     */
    @Enumerated(STRING)
    @Column(nullable = false)
    private PushMsgType pushMsgType;

    // 트랙,앨범,댓글 2차 구분값
    @Enumerated(STRING)
    @Column(nullable = false)
    private ContentsType contentType;

    // 앨범,트랙,댓글 해당 ID 정보를 담을 컬럼
    private Long contentsId;

    // False : 읽지 않음, True: 읽음
    @Column(nullable = false)
    private Boolean isRead;


    public static UserPushMessages create(User toUser,User fromUser,PushMsgType pushMsgType, ContentsType contentType,Long contentsId) {
        UserPushMessages userPushMessages = new UserPushMessages();
        userPushMessages.setToUser(toUser);
        userPushMessages.setFromUser(fromUser);
        userPushMessages.setPushMsgType(pushMsgType);
        userPushMessages.setContentType(contentType);
        userPushMessages.setIsRead(false);
        userPushMessages.setContentsId(contentsId);
        return userPushMessages;
    }


}
