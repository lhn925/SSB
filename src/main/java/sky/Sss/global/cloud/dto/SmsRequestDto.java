package sky.Sss.global.cloud.dto;


import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
@Builder
public class SmsRequestDto {

    private String type;

    private String contentType;

    // 82
    private String countryCode;
    // 보내는 사람의 전화번호
    private String from;
    private String content;
    private List<MessageDto> messages;

}