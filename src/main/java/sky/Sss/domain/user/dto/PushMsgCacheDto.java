package sky.Sss.domain.user.dto;


import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.user.entity.UserPushMessages;
import sky.Sss.domain.user.model.ContentsType;
import sky.Sss.domain.user.model.PushMsgType;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
public class PushMsgCacheDto {



    // 알림 ID
    private Long id;

    // 보낸 사람에 유저 고유 아이디 값
    private Long fromUid;

    // 댓글,트랙,playList
    private Long contentsId;

    private ContentsType contentType;

    private PushMsgType pushMsgType;

    // 실시간 알림으로 보내졌는지 확인
    private Boolean isPush;

    private Boolean isRead;
    // 보낸 날짜
    private LocalDateTime createdDateTime;

    public static PushMsgCacheDto create (UserPushMessages userPushMessages,boolean isPush) {
        PushMsgCacheDto pushMsgCacheDto = new PushMsgCacheDto();
        pushMsgCacheDto.setId(userPushMessages.getId());
        pushMsgCacheDto.setFromUid(userPushMessages.getFromUser().getId());
        pushMsgCacheDto.setContentsId(userPushMessages.getContentsId());
        pushMsgCacheDto.setContentType(userPushMessages.getContentType());
        pushMsgCacheDto.setPushMsgType(userPushMessages.getPushMsgType());
        pushMsgCacheDto.setIsRead(userPushMessages.getIsRead());
        pushMsgCacheDto.setIsPush(isPush);
        pushMsgCacheDto.setCreatedDateTime(userPushMessages.getCreatedDateTime());
        return pushMsgCacheDto;
    }




}
