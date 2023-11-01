package sky.Sss.domain.user.entity;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.model.ChangeSuccess;
import sky.Sss.global.base.BaseEntity;
import sky.Sss.global.base.BaseTimeEntity;
import sky.Sss.global.base.login.DefaultLoginLog;
import sky.Sss.global.locationfinder.dto.UserLocationDto;
import sky.Sss.global.locationfinder.service.LocationFinderService;

/**
 * 유저정보 수정내역
 */
@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserActivityLog extends BaseTimeEntity {
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

    //사용자 고유번호
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "uid")
    private User uId;

    // 변경 내용
    private String chaContent;

    //변경 방법
    private String chaMethod;

    // 변경 성공 여부
    @Enumerated(STRING)
    private ChangeSuccess changeSuccess;
    @Embedded
    private DefaultLoginLog defaultLog;

    @LastModifiedBy
    private String modifiedByUserId;

    @Builder
    public UserActivityLog(
        User user, ChangeSuccess changeSuccess, String chaContent, String chaMethod, DefaultLoginLog defaultLoginLog) {
        this.uId = user;
        this.changeSuccess = changeSuccess;
        this.chaContent = chaContent;
        this.chaMethod = chaMethod;
        this.defaultLog = defaultLoginLog;
    }

    public static UserActivityLog createActivityLog(User user, LocationFinderService locationFinderService,
        String chaContent,
        String chaMethod, String userAgent, ChangeSuccess changeSuccess, Status isStatus) {
        UserLocationDto userLocationDto = null;
        userLocationDto = locationFinderService.findLocation();

        DefaultLoginLog defaultLog = DefaultLoginLog.createDefaultLoginLog(isStatus, userLocationDto,userAgent);
        return UserActivityLog.builder()
            .user(user)
            .changeSuccess(changeSuccess) // 실패 여부 확인
            .chaContent(chaContent)
            .chaMethod(chaMethod)
            .defaultLoginLog(defaultLog)
            .build();
    }


}
