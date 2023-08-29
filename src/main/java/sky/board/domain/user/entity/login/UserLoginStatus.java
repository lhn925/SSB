package sky.board.domain.user.entity.login;

import static jakarta.persistence.FetchType.LAZY;

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
import sky.board.domain.user.entity.User;
import sky.board.domain.user.model.Status;
import sky.board.domain.user.model.UserAgent;
import sky.board.global.base.BaseTimeEntity;
import sky.board.global.base.login.DefaultLoginLog;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserLoginStatus extends BaseTimeEntity{

    @Id
    @GeneratedValue
    private Long id;

    //key 값
    private String redisKey;

    private String ip;

    //사용자 고유 번호
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "uId")
    private User uId;

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
    private UserLoginStatus(String redisKey, String ip, User uId, String userId, String countryName, String os,
        UserAgent userAgent, String browser, String latitude, String longitude, Status isStatus,
        Boolean loginStatus) {
        this.redisKey = redisKey;
        this.ip = ip;
        this.uId = uId;
        this.os = os;
        this.browser = browser;
        this.loginStatus = loginStatus;
        this.defaultLoginLog = DefaultLoginLog.builder()
            .userId(userId)
            .countryName(countryName)
            .userAgent(userAgent)
            .longitude(longitude)
            .latitude(latitude)
            .isStatus(isStatus.getValue()).build();
    }




}
