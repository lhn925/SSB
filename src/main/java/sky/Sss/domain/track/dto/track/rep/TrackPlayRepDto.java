package sky.Sss.domain.track.dto.track;

import static lombok.AccessLevel.PRIVATE;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.dto.track.log.TrackPlayLogRepDto;
import sky.Sss.domain.track.entity.track.SsbTrack;

@Getter
@Setter(PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrackPlayRepDto extends TrackInfoSimpleDto implements Serializable {
    private TrackPlayLogRepDto trackPlayLogRepDto;

    @Builder
    private TrackPlayRepDto(SsbTrack ssbTrack) {
        super(ssbTrack.getId(), ssbTrack.getToken(), ssbTrack.getTitle(), ssbTrack.getUser(),
            ssbTrack.getTrackLength(), ssbTrack.getCoverUrl(), ssbTrack.getIsPrivacy(), ssbTrack.getCreatedDateTime());
    }

    public static TrackPlayRepDto create(SsbTrack ssbTrack) {
        // 서버 호출 시간
        return TrackPlayRepDto.builder().ssbTrack(ssbTrack).build();
    }
    public static void updateTrackPlayLogRepDto(TrackPlayRepDto trackPlayRepDto,
        TrackPlayLogRepDto trackPlayLogRepDto) {
        // 조회수 측정 정보
        trackPlayRepDto.setTrackPlayLogRepDto(trackPlayLogRepDto);
    }
}
