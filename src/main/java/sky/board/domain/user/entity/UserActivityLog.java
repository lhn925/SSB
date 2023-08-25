package sky.board.domain.user.entity;

import static jakarta.persistence.EnumType.STRING;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import sky.board.domain.user.model.LoginSuccess;
import sky.board.domain.user.model.Status;
import sky.board.domain.user.model.UserAgent;
import sky.board.domain.user.model.ChangeSuccess;
import sky.board.global.locationfinder.dto.UserLocationDto;
import sky.board.global.locationfinder.service.LocationFinderService;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
public class UserActivityLog {
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

    //변경 유저
    private String userId;

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


    // 변경일시
    @CreatedDate
    @DateTimeFormat(pattern = "yyyy:MM:dd HH:mm:ss")
    private LocalDateTime activityLogDateTime;

    @Builder
    public UserActivityLog(String ip,
        Long uId,
        String userId, String countryName, ChangeSuccess changeSuccess,
        UserAgent userAgent, String latitude, String longitude, Status isStatus, String chaContent, String chaMethod) {
        this.ip = ip;
        this.uId = uId;
        this.userId = userId;
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
        String chaMethod, HttpServletRequest request,
        String userId, ChangeSuccess changeSuccess, Status isStatus) {
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
            .userId(userId) //유저아이디 저장
            .isStatus(isStatus)
            .chaContent(chaContent)
            .chaMethod(chaMethod)
            .build();
    }

    protected UserActivityLog() {

    }


}