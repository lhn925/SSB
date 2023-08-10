package sky.board.domain.user.entity;


import static jakarta.persistence.EnumType.ORDINAL;
import static jakarta.persistence.EnumType.STRING;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Locale;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import sky.board.domain.user.model.LoginSuccess;
import sky.board.domain.user.model.Status;
import sky.board.domain.user.model.UserAgent;

/**
 * 같은 아이디 당 시도
 * <p>
 * 5번이상 틀렸을 경우
 */

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
public class UserLoginLog {

    @Id
    @GeneratedValue
    private Long id;

    private String ip;

    private String userId;

    // 성공 여부
    @Enumerated(STRING)
    private LoginSuccess isSuccess;

    // 로그인시도 국가
    private String countryName;

    // 접속한 기기
    @Enumerated(STRING)
    private UserAgent userAgent;

    //위도
    private String latitude;

    // 경도
    private String longitude;

    // 상태 값
    private Boolean isStatus;


    @CreatedDate
    @DateTimeFormat(pattern = "yyyy:MM:dd HH:mm:ss")
    private LocalDateTime loginDateTime;

    @Builder
    public UserLoginLog(String ip, String userId, LoginSuccess isSuccess, String countryName,
        UserAgent userAgent,
        String latitude, String longitude,Status isStatus) {
        this.ip = ip;
        this.userId = userId;
        this.isSuccess = isSuccess;
        this.countryName = countryName;
        this.userAgent = userAgent;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isStatus = isStatus.getValue();
    }

    protected UserLoginLog() {

    }

    public static UserAgent isDevice(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent").toUpperCase();
        if (userAgent.indexOf(UserAgent.MOBI.name()) > -1) {
            return UserAgent.MOBI;
        } else {
            return UserAgent.PC;
        }
    }

}
