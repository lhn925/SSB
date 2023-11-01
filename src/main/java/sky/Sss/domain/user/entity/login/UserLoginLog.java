package sky.Sss.domain.user.entity.login;


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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import sky.Sss.domain.user.model.LoginSuccess;
import sky.Sss.domain.user.model.Status;
import sky.Sss.global.base.BaseTimeEntity;
import sky.Sss.global.base.login.DefaultLoginLog;
import sky.Sss.global.locationfinder.dto.UserLocationDto;
import sky.Sss.global.locationfinder.service.LocationFinderService;

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

    @Embedded
    private DefaultLoginLog defaultLoginLog;

    @Builder
    private UserLoginLog(Long uId, LoginSuccess isSuccess, DefaultLoginLog defaultLoginLog) {
        this.uId = uId == null ? 0 : uId;
        this.isSuccess = isSuccess;
        this.defaultLoginLog = defaultLoginLog;
    }


    public static UserLoginLog getLoginLog(Long uId, LocationFinderService locationFinderService,String userAgent, LoginSuccess isSuccess, Status isStatus) {

        UserLocationDto userLocationDto = null;
            userLocationDto = locationFinderService.findLocation();
        UserLoginLog userLoginLog = UserLoginLog.builder()
            .uId(uId)
            .isSuccess(isSuccess)
            .defaultLoginLog(DefaultLoginLog.createDefaultLoginLog(
                    isStatus, userLocationDto, userAgent
                )
            )
            .build();
        return userLoginLog;
    }

}
