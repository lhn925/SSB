package sky.Sss.global.base.login;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Enumerated;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.model.UserAgent;
import sky.Sss.global.locationfinder.dto.UserLocationDto;

@Slf4j
@Getter
@Setter(value = PRIVATE)
@Embeddable
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = PROTECTED)
public class DefaultLoginLog {

    private String ip;
    // 로그인시도 국가
    private String countryName;

    @CreatedBy
    private String userId;
    // 접속한 기기
    @Enumerated(STRING)
    private UserAgent userAgent;
    //위도
    private String latitude;

    // 경도
    private String longitude;

    // 상태값 True면 활성화 , False면 비활성화
    private Boolean isStatus;

    @Builder
    public DefaultLoginLog(String ip, String countryName, UserAgent userAgent, String latitude, String longitude,
        Boolean isStatus) {
        this.ip = ip;
        this.countryName = countryName;
        this.userAgent = userAgent;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isStatus = isStatus;
    }

    public static DefaultLoginLog createDefaultLoginLog(Status isStatus,
        UserLocationDto userLocationDto,
        String userAgent) {

        return DefaultLoginLog.builder()
            .ip(userLocationDto.getIpAddress()) //ip 저장
            .countryName(userLocationDto.getCountryName()) // iso Code 저장
            .latitude(userLocationDto.getLatitude()) // 위도
            .longitude(userLocationDto.getLongitude()) // 경도
            .userAgent(UserLocationDto.isDevice(userAgent)) // 기기 저장
            .isStatus(isStatus.getValue()).build();
    }


    public void changeIsStatus(Boolean isStatus) {
        this.setIsStatus(isStatus);
    }


}
