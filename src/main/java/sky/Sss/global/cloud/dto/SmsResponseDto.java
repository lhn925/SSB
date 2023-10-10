package sky.Sss.global.cloud.dto;
import java.time.LocalDateTime;
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
public class SmsResponseDto {
    private String requestId;
    // 요청시간
    private LocalDateTime requestTime;
    // 완료시간
    private String statusCode;
    private String statusName;

}
