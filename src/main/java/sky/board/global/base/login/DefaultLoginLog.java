package sky.board.global.base.login;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sky.board.domain.user.model.UserAgent;
@Getter
@Embeddable
@NoArgsConstructor(access = PROTECTED)
public class DefaultLoginLog {
    // 로그인시도 국가
    private String countryName;
    private String userId;
    // 접속한 기기
    @Enumerated(STRING)
    private UserAgent userAgent;
    //위도
    private String latitude;

    // 경도
    private String longitude;

    // 상태 값
    private Boolean isStatus;

    @Builder
    public DefaultLoginLog(String countryName, UserAgent userAgent, String latitude, String longitude,
        Boolean isStatus,String userId) {
        this.countryName = countryName;
        this.userAgent = userAgent;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isStatus = isStatus;
        this.userId = userId;
    }

}
