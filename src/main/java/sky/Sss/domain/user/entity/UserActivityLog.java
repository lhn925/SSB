package sky.Sss.domain.user.entity;

import static jakarta.persistence.EnumType.STRING;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.model.UserAgent;
import sky.Sss.domain.user.model.ChangeSuccess;
import sky.Sss.global.base.BaseEntity;
import sky.Sss.global.locationfinder.dto.UserLocationDto;
import sky.Sss.global.locationfinder.service.LocationFinderService;

/**
 *
 * 유저정보 수정내역
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserActivityLog extends BaseEntity {
//    비밀번호 변경(최근 6개월 내) 및 연락처 수정 이력을 제공합니다.
    /**
     * 일시
     * 변경 내용
     * 변경 방법
     * 변경 IP
     */
    @Id
    @GeneratedValue
    private Long id;

    //변경 아이피
    private String ip;
    //사용자 고유번호
    private Long uId;

    // 변경 내용
    private String chaContent;

    //변경 방법
    private String chaMethod;

    // 변경 시도 국가
    private String countryName;

    // 변경 성공 여부
    @Enumerated(STRING)
    private ChangeSuccess changeSuccess;


    // 변경에 사용된 기기
    @Enumerated(STRING)
    private UserAgent userAgent;

    //위도
    private String latitude;

    // 경도
    private String longitude;

    // 상태 값
    private Boolean isStatus;

    @Builder
    public UserActivityLog(String ip,
        Long uId, String countryName, ChangeSuccess changeSuccess,
        UserAgent userAgent, String latitude, String longitude, Status isStatus, String chaContent, String chaMethod) {
        this.ip = ip;
        this.uId = uId;
        this.countryName = countryName;
        this.changeSuccess = changeSuccess;
        this.userAgent = userAgent;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isStatus = isStatus.getValue();
        this.chaContent = chaContent;
        this.chaMethod = chaMethod;

    }

    public static UserActivityLog getActivityLog(Long uId,LocationFinderService locationFinderService, String chaContent,
        String chaMethod, HttpServletRequest request, ChangeSuccess changeSuccess, Status isStatus) {
        UserLocationDto userLocationDto = null;
        try {
            userLocationDto = locationFinderService.findLocation();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (GeoIp2Exception e) {
            throw new RuntimeException(e);
        }
        return UserActivityLog.builder()
            .uId(uId)
            .ip(userLocationDto.getIpAddress()) //ip 저장
            .countryName(userLocationDto.getCountryName()) // iso Code 저장
            .latitude(userLocationDto.getLatitude()) // 위도
            .longitude(userLocationDto.getLongitude()) // 경도
            .changeSuccess(changeSuccess) // 실패 여부 확인
            .userAgent(UserLocationDto.isDevice(request)) // 기기 저장
            .isStatus(isStatus)
            .chaContent(chaContent)
            .chaMethod(chaMethod)
            .build();
    }


}
