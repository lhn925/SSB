package sky.board.domain.user.entity.login;

import static jakarta.persistence.FetchType.LAZY;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;
import sky.board.domain.user.dto.UserInfoSessionDto;
import sky.board.domain.user.entity.User;
import sky.board.domain.user.model.LoginSuccess;
import sky.board.domain.user.model.Status;
import sky.board.domain.user.model.UserAgent;
import sky.board.domain.user.utili.CustomCookie;
import sky.board.global.base.BaseTimeEntity;
import sky.board.global.base.login.DefaultLoginLog;
import sky.board.global.locationfinder.dto.UserLocationDto;
import sky.board.global.locationfinder.service.LocationFinderService;
import sky.board.global.redis.dto.RedisKeyDto;

@Entity
@Getter
@Setter(value = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserLoginStatus extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    //사용자의 remember key 값
    private String remember;
    //사용자가 접속한 세션에 키 값

    @Column(nullable = false)
    private String session;

    //사용자 고유 번호
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "uid")
    private User uid;

    //운영체제
    private String os;

    //접속한 브라우저
    private String browser;

    @Embedded
    private DefaultLoginLog defaultLoginLog;

    // 로그인 상태 값 혹은 관리
    // true:login , false: logout
    private Boolean loginStatus;


    @Builder
    private UserLoginStatus(String remember,String session, User uid, String os,
        String browser, DefaultLoginLog defaultLoginLog,
        Boolean loginStatus) {
        this.remember = remember;
        this.session = session;
        this.uid = uid;
        this.os = os;
        this.browser = browser;
        this.loginStatus = loginStatus;
        this.defaultLoginLog = defaultLoginLog;
    }


    /**
     *
     * 객체 생성
     * @param userInfoSessionDto
     * @param locationFinderService
     * @param request
     * @param user
     * @return
     */
    public static UserLoginStatus getLoginStatus(UserInfoSessionDto userInfoSessionDto,
        LocationFinderService locationFinderService,
        HttpServletRequest request, User user) {
        UserLocationDto userLocationDto;
        try {
            userLocationDto = locationFinderService.findLocation();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (GeoIp2Exception e) {
            throw new RuntimeException(e);
        }

        UserLoginStatusBuilder builder = UserLoginStatus.builder()
            .uid(user)
            .loginStatus(Status.ON.getValue())
            .defaultLoginLog(
                DefaultLoginLog.createDefaultLoginLog(user.getUserId(), Status.ON, userLocationDto, request)
            ).session(request.getSession().getId())
            .browser(UserLocationDto.getClientBrowser(request))
            .os(UserLocationDto.getClientOS(request));
        String rememberMe = CustomCookie.readCookie(request.getCookies(), "RememberMe");

        /**
         * rememberMe가 있을 경우
         */
        if (rememberMe != null && StringUtils.hasText(rememberMe)) {
            builder.remember(rememberMe);
        }
        return builder.build();
    }


    /**
     * 로그아웃 상태 및
     * isStatus 상태 변경
     * @param userLoginStatus
     */
    public static void loginStatusUpdate(UserLoginStatus userLoginStatus) {
        userLoginStatus.setLoginStatus(Status.OFF.getValue());
        userLoginStatus.getDefaultLoginLog().changeIsStatus(Status.OFF.getValue());
    }

}
