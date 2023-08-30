package sky.board.global.base.login;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Enumerated;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.board.domain.user.model.Status;
import sky.board.domain.user.model.UserAgent;
import sky.board.global.locationfinder.dto.UserLocationDto;

@Getter
@Setter(value = PRIVATE)
@Embeddable
@NoArgsConstructor(access = PROTECTED)
public class DefaultLoginLog {

    private String ip;
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

    // 상태값 True면 활성화 , False면 비활성화
    private Boolean isStatus;

    @Builder
    public DefaultLoginLog(String ip, String countryName, UserAgent userAgent, String latitude, String longitude,
        Boolean isStatus, String userId) {
        this.ip = ip;
        this.countryName = countryName;
        this.userAgent = userAgent;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isStatus = isStatus;
        this.userId = userId;
    }

    public static DefaultLoginLog createDefaultLoginLog(String userId, Status isStatus,
        UserLocationDto userLocationDto,
        HttpServletRequest request) {
        return DefaultLoginLog.builder()
            .ip(userLocationDto.getIpAddress()) //ip 저장
            .countryName(userLocationDto.getCountryName()) // iso Code 저장
            .latitude(userLocationDto.getLatitude()) // 위도
            .longitude(userLocationDto.getLongitude()) // 경도
            .userAgent(UserLocationDto.isDevice(request)) // 기기 저장
            .userId(userId) //유저아이디 저장
            .isStatus(isStatus.getValue()).build();
    }


    public void changeIsStatus(Boolean isStatus) {
        this.setIsStatus(isStatus);
    }


}
