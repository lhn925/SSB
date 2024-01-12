package sky.Sss.domain.track.entity.chart;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.model.ChartStatus;
import sky.Sss.domain.track.model.PlayBackStatus;
import sky.Sss.domain.track.model.TrackMinimumPlayTime;
import sky.Sss.domain.user.entity.User;
import sky.Sss.global.base.BaseTimeEntity;
import sky.Sss.global.base.login.DefaultLocationLog;
import sky.Sss.global.base.login.DeviceDetails;

/**
 * 플레이 로그 테이블
 */
@Entity
@Getter
@Setter(value = PRIVATE)
@NoArgsConstructor(access = PROTECTED)
public class SsbTrackAllPlayLogs extends BaseTimeEntity {

    @Id
    @GeneratedValue
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
    private Integer minimumPlayTime;

    // 재생 시작 시간
    @Column(nullable = false)
    private Long startTime;
    // 재생 종료 시간
    // 재생 종료시간이 두 시간대에 겹쳐있으면
    // 차트에 반영 안됨
    private Long closeTime;

    // 재생횟수에 반영 여부 구분 컬럼
    @Column(nullable = false)
    @Enumerated(value = STRING)
    private PlayBackStatus playBackStatus;

    // 차트에 반영 되는 로그인지 구분
    @Column(nullable = false)
    @Enumerated(value = STRING)
    private ChartStatus chartStatus;

    @Embedded
    private DefaultLocationLog defaultLocationLog;
    @Embedded
    private DeviceDetails deviceDetails;

    public static SsbTrackAllPlayLogs create(User user, SsbTrack ssbTrack,
        DefaultLocationLog defaultLocationLog,
        DeviceDetails deviceDetails, Long startTime) {
        SsbTrackAllPlayLogs ssbTrackAllPlayLogs = new SsbTrackAllPlayLogs();
        ssbTrackAllPlayLogs.setUser(user);
        ssbTrackAllPlayLogs.setSsbTrack(ssbTrack);
        ssbTrackAllPlayLogs.setDefaultLocationLog(defaultLocationLog);
        ssbTrackAllPlayLogs.setDeviceDetails(deviceDetails);
        ssbTrackAllPlayLogs.setStartTime(startTime);

        Integer trackMiniNum = TrackMinimumPlayTime.MINI_NUM_SECOND.getSeconds();

        // 전체 트랙길이가 60초보다 작을 경우
        if (ssbTrack.getTrackLength() < trackMiniNum) {
            trackMiniNum = ssbTrack.getTrackLength();
        }

        ssbTrackAllPlayLogs.setMinimumPlayTime(trackMiniNum);

        // 처음 생성시 플레이 횟수에 반영 안됨 으로 설정
        SsbTrackAllPlayLogs.updatePlayStatus(ssbTrackAllPlayLogs,PlayBackStatus.INCOMPLETE);
        return ssbTrackAllPlayLogs;
    }

    public static void updatePlayStatus(SsbTrackAllPlayLogs ssbTrackAllPlayLogs, PlayBackStatus playBackStatus) {
        ssbTrackAllPlayLogs.setPlayBackStatus(playBackStatus);
    }

    public static void updateChatStatus(SsbTrackAllPlayLogs ssbTrackAllPlayLogs, ChartStatus chartStatus) {
        ssbTrackAllPlayLogs.setChartStatus(chartStatus);
    }

    public static void updateTotalPlayTime(SsbTrackAllPlayLogs ssbTrackAllPlayLogs, Integer playTime) {
        ssbTrackAllPlayLogs.setTotalPlayTime(playTime);
    }

    public static void updateCloseTime(SsbTrackAllPlayLogs ssbTrackAllPlayLogs, Long closeTime) {
        ssbTrackAllPlayLogs.setStartTime(closeTime);
    }


}
