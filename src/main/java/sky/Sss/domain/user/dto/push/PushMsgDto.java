package sky.Sss.domain.user.dto.push;


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
public class PushMsgDto {


    /**
     * 이동할 링크
     * 컨텐츠 내용
     */
    // 알림 Id
    private Long id;

    // fromUser 의 닉네임
    private String fromUserName;

    // 알림 문구에 쓰일 value
    private String insertedText;

    private String linkUrl;

    // PushMegType
    private PushMsgType pushMsgType;

    // contentType
    private ContentsType contentsType;

    // 읽음 유무
    private Boolean isRead;

    // 보낸 날짜
    private LocalDateTime createdDateTime;


    public static PushMsgDto create(UserPushMessages userPushMessages, String insertedText, String linkUrl) {
        PushMsgDto pushMsgDto = new PushMsgDto();

        PushMsgType pushMsgType = userPushMessages.getPushMsgType();
        ContentsType contentType = userPushMessages.getContentsType();
        pushMsgDto.setPushMsgType(pushMsgType);
        pushMsgDto.setContentsType(contentType);

        pushMsgDto.setId(userPushMessages.getId());

        pushMsgDto.setFromUserName(userPushMessages.getFromUser().getUserName());
        pushMsgDto.setIsRead(userPushMessages.getIsRead());

        pushMsgDto.setInsertedText(insertedText);

        pushMsgDto.setLinkUrl(linkUrl);

        pushMsgDto.setCreatedDateTime(userPushMessages.getCreatedDateTime());
        return pushMsgDto;
    }


}
