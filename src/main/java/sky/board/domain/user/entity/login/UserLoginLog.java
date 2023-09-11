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
import java.util.Locale.IsoCountryCode;
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
import sky.board.global.base.BaseEntity;
import sky.board.global.base.BaseTimeEntity;
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
public class UserLoginLog extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

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
    private UserLoginLog(Long uId, LoginSuccess isSuccess, DefaultLoginLog defaultLoginLog) {
        this.uId = uId == null ? 0 : uId;
        this.isSuccess = isSuccess;
        this.defaultLoginLog = defaultLoginLog;
    }


    public static UserLoginLog getLoginLog(Long uId, LocationFinderService locationFinderService,
        HttpServletRequest request, LoginSuccess isSuccess, Status isStatus) {

        UserLocationDto userLocationDto = null;
        try {
            userLocationDto = locationFinderService.findLocation();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (GeoIp2Exception e) {
            throw new RuntimeException(e);
        }
        UserLoginLog userLoginLog = UserLoginLog.builder()
            .uId(uId)
            .isSuccess(isSuccess)
            .defaultLoginLog(DefaultLoginLog.createDefaultLoginLog(
                    isStatus, userLocationDto, request
                )
            )
            .build();
        return userLoginLog;
    }

}
