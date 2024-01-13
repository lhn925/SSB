package sky.Sss.domain.user.entity.login;

import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.utili.jwt.JwtTokenDto;
import sky.Sss.global.base.BaseTimeEntity;
import sky.Sss.global.base.login.DefaultLocationLog;
import sky.Sss.global.base.login.DeviceDetails;
import sky.Sss.global.locationfinder.dto.UserLocationDto;
import sky.Sss.global.locationfinder.service.LocationFinderService;

@Slf4j
@Entity
@Getter
@Setter(value = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserLoginStatus extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    //사용자에게 부여한 Jwt redisToken 값
    @Column(nullable = false)
    private String redisToken;


    @Column(nullable = false)
    private String refreshToken;

    private String wsId;

    @Column(nullable = false)
    private String sessionId;


    //사용자 고유 번호
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "uid")
    private User uid;

    //운영체제
    private String os;

    //접속한 브라우저
    private String browser;

    @Embedded
    private DefaultLocationLog defaultLocationLog;

    // 로그인 상태 값 혹은 관리
    // true:login , false: logout
    private Boolean loginStatus;


    @Builder
    private UserLoginStatus(String redisToken, User uid, String os,
        String browser, DefaultLocationLog defaultLocationLog, String refreshToken,
        Boolean loginStatus, String sessionId) {

        this.refreshToken = refreshToken;
        this.redisToken = redisToken;
        this.uid = uid;
        this.os = os;
        this.browser = browser;
        this.sessionId = sessionId;
        this.loginStatus = loginStatus;
        this.defaultLocationLog = defaultLocationLog;
    }


    /**
     * 객체 생성
     *
     * @param locationFinderService
     * @param user
     * @return
     */
    public static UserLoginStatus getLoginStatus(
        LocationFinderService locationFinderService,
        String userAgent, User user, JwtTokenDto jwtTokenDto, String sessionId) {
        UserLocationDto userLocationDto;
        userLocationDto = locationFinderService.findLocation();

        UserLoginStatusBuilder builder = UserLoginStatus.builder()
            .uid(user)
            .loginStatus(Status.ON.getValue())
            .refreshToken(jwtTokenDto.getRefreshToken())
            .defaultLocationLog(
                DefaultLocationLog.createDefaultLocationLog(Status.ON, userLocationDto, userAgent)
            ).redisToken(jwtTokenDto.getRedisToken())
            .sessionId(sessionId)
            .browser(DeviceDetails.getClientBrowser(userAgent))
            .os(DeviceDetails.getClientOS(userAgent));

        return builder.build();
    }


    /**
     * 로그아웃 상태 및
     * isStatus 상태 변경
     *
     * @param userLoginStatus
     */
    public static void loginStatusUpdate(UserLoginStatus userLoginStatus, Status loginStatus, Status isStatus) {
        userLoginStatus.setLoginStatus(loginStatus.getValue());
        userLoginStatus.getDefaultLocationLog().changeIsStatus(loginStatus.getValue());
    }

    public static void wsIdUpdate(UserLoginStatus userLoginStatus, String wsId) {
        userLoginStatus.setWsId(wsId);
    }
}
