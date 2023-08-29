package sky.board.domain.user.entity.login;


import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import sky.board.domain.user.model.LoginSuccess;
import sky.board.domain.user.model.Status;
import sky.board.domain.user.model.UserAgent;
import sky.board.global.base.login.DefaultLoginLog;
import sky.board.global.locationfinder.dto.UserLocationDto;
import sky.board.global.locationfinder.service.LocationFinderService;

/**
 * 같은 아이디 당 시도
 * <p>
 * 5번이상 틀렸을 경우
 */

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = PROTECTED)
public class UserLoginLog {

    @Id
    @GeneratedValue
    private Long id;

    private String ip;

    //사용자 고유 번호
    private Long uId;

    // 성공 여부
    @Enumerated(STRING)
    private LoginSuccess isSuccess;

    @CreatedDate
    @DateTimeFormat(pattern = "yyyy:MM:dd HH:mm:ss")
    private LocalDateTime loginDateTime;

    @Embedded
    private DefaultLoginLog defaultLoginLog;

    @Builder
    private UserLoginLog(String ip,
        Long uId,
        String userId, LoginSuccess isSuccess, String countryName,
        UserAgent userAgent,
        String latitude, String longitude, Status isStatus) {
        this.ip = ip;
        this.uId = uId == null ? 0 : uId;
        this.isSuccess = isSuccess;
        this.defaultLoginLog = DefaultLoginLog.builder()
            .userId(userId)
            .countryName(countryName)
            .userAgent(userAgent)
            .longitude(longitude)
            .latitude(latitude)
            .isStatus(isStatus.getValue()).build();
    }


    public static UserLoginLog getLoginLog(Long uId, LocationFinderService locationFinderService,
        HttpServletRequest request, LoginSuccess isSuccess, Status isStatus) {
        String userId = request.getParameter("userId");

        UserLocationDto userLocationDto = null;
        try {
            userLocationDto = locationFinderService.findLocation();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (GeoIp2Exception e) {
            throw new RuntimeException(e);
        }
        UserLoginLog userLoginLog = UserLoginLog.builder()
            .ip(userLocationDto.getIpAddress()) //ip 저장
            .uId(uId)
            .countryName(userLocationDto.getCountryName()) // iso Code 저장
            .latitude(userLocationDto.getLatitude()) // 위도
            .longitude(userLocationDto.getLongitude()) // 경도
            .isSuccess(isSuccess) // 실패 여부 확인
            .userAgent(UserLocationDto.isDevice(request)) // 기기 저장
            .userId(userId) //유저아이디 저장
            .isStatus(isStatus)
            .build();
        return userLoginLog;
    }

}
