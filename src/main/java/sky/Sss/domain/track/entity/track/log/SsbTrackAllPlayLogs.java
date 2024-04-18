package sky.Sss.domain.track.entity.track.log;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.model.ChartStatus;
import sky.Sss.domain.track.model.PlayStatus;
import sky.Sss.domain.track.model.TrackMinimumPlayTime;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.utili.TokenUtil;
import sky.Sss.global.base.BaseTimeEntity;
import sky.Sss.global.base.login.DefaultLocationLog;
import sky.Sss.global.base.login.DeviceDetails;
import sky.Sss.global.utili.DayTime;

/**
 * 플레이 로그 테이블
 */
@Entity
@Getter
@Setter(value = PRIVATE)
@NoArgsConstructor(access = PROTECTED)
public class SsbTrackAllPlayLogs extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String token;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "uid")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "track_id")
    private SsbTrack ssbTrack;

    // 유저가 총 재생한 시간
    private Integer totalPlayTime;

    // 최소 재생 시간
    @Column(nullable = false)
    private Integer minimumPlayTime;

    // 재생 시작 시간
    @Column(nullable = false)
    private Long startTime;
    // 재생 종료 시간
    // 재생 종료시간이 두 시간대에 겹쳐있으면
    // 차트에 반영 안됨
    @Column(nullable = false)
    private Long closeTime;


    // 파일 접근 제한 시간
    @Column(nullable = false)
    private Long expireTime;

    // 최소조건은 채웠고 재생횟수에 반영이 되는지 확인 값 컬럼
    @Column(nullable = false)
    @Enumerated(value = STRING)
    private PlayStatus playStatus;

    // 차트에 반영 되는 로그 인지 구분
    @Column(nullable = false)
    @Enumerated(value = STRING)
    private ChartStatus chartStatus;


    // 해당 컬럼이 false 면은
    // 플레이 가능 상태
    // true 면 플레이 불가능 상태
    // 유저가 track play 시 true 면은 재생 불가능
    @Column(nullable = false)
    private Boolean isValid;

//
//    @OneToOne(mappedBy = "ssbTrackAllPlayLogs",cascade = ALL,fetch = LAZY)
//    private SsbChartIncludedPlays ssbChartIncludedPlays;

    @Embedded
    private DefaultLocationLog defaultLocationLog;
    @Embedded
    private DeviceDetails deviceDetails;

    public static SsbTrackAllPlayLogs create(User user, SsbTrack ssbTrack,
        DefaultLocationLog defaultLocationLog,
        DeviceDetails deviceDetails, Long startTime, LocalDateTime createdDateTime) {
        SsbTrackAllPlayLogs ssbTrackAllPlayLogs = new SsbTrackAllPlayLogs();
        ssbTrackAllPlayLogs.setUser(user);
        ssbTrackAllPlayLogs.setToken(TokenUtil.getToken());
        ssbTrackAllPlayLogs.setSsbTrack(ssbTrack);
        ssbTrackAllPlayLogs.setDefaultLocationLog(defaultLocationLog);
        ssbTrackAllPlayLogs.setDeviceDetails(deviceDetails);
        ssbTrackAllPlayLogs.setStartTime(removeMillis(startTime));
        ssbTrackAllPlayLogs.setIsValid(false);


        Integer trackMiniNum = TrackMinimumPlayTime.MINI_NUM_SECOND.getSeconds();

        // 전체 트랙길이가 60초보다 작을 경우
        if (ssbTrack.getTrackLength() < trackMiniNum) {
            trackMiniNum = ssbTrack.getTrackLength();
        }
        ssbTrackAllPlayLogs.setMinimumPlayTime(trackMiniNum);
        ssbTrackAllPlayLogs.setCreatedDateTime(createdDateTime);
        // 처음 생성시 플레이 횟수에 반영 안됨 으로 설정
        SsbTrackAllPlayLogs.updatePlayStatus(ssbTrackAllPlayLogs, 0);


        // 제한 시간 설정
        Instant zoneInstant = DayTime.localDateTimeToEpochMillis(createdDateTime.plusHours(1));
        ssbTrackAllPlayLogs.setExpireTime(zoneInstant.toEpochMilli());

        return ssbTrackAllPlayLogs;
    }



    public static void updatePlayStatus(SsbTrackAllPlayLogs ssbTrackAllPlayLogs, int playTime) {

        if (playTime != 0) {
            ssbTrackAllPlayLogs.setPlayStatus(PlayStatus.getPlayStatus(ssbTrackAllPlayLogs.getMinimumPlayTime() <= playTime));
            return;
        }
        ssbTrackAllPlayLogs.setPlayStatus(PlayStatus.INCOMPLETE);

    }

    public static void updateChartStatus(SsbTrackAllPlayLogs ssbTrackAllPlayLogs, boolean isStatus) {
        ssbTrackAllPlayLogs.setChartStatus(ChartStatus.getChartStatus(isStatus));
    }

    public static void updateTotalPlayTime(SsbTrackAllPlayLogs ssbTrackAllPlayLogs, Integer playTime) {
        ssbTrackAllPlayLogs.setTotalPlayTime(playTime);
    }

    public static void updateCloseTime(SsbTrackAllPlayLogs ssbTrackAllPlayLogs, Long closeTime) {
        ssbTrackAllPlayLogs.setCloseTime(removeMillis(closeTime));
    }

    public static void updateIsValid(SsbTrackAllPlayLogs ssbTrackAllPlayLogs, boolean isValid) {
        ssbTrackAllPlayLogs.setIsValid(isValid);
    }

    public static long removeMillis (Long millisSeconds) {
        return (millisSeconds / 1000) * 1000;
    }

}
