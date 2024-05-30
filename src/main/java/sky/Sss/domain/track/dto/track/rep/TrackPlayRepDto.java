package sky.Sss.domain.track.dto.track.rep;

import static lombok.AccessLevel.PRIVATE;

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.dto.track.common.TrackInfoSimpleDto;
import sky.Sss.domain.track.dto.track.log.TrackPlayLogRepDto;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.user.dto.myInfo.UserProfileRepDto;

@Getter
@Setter(PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrackPlayRepDto extends TrackInfoSimpleDto implements Serializable {
    private TrackPlayLogRepDto trackPlayLogRepDto;

    private TrackPlayRepDto(SsbTrack ssbTrack) {
        super(
            ssbTrack.getId(),
            ssbTrack.getTitle(),
            ssbTrack.getTrackLength(),
            TrackInfoSimpleDto.getCoverUrl(ssbTrack.getCoverUrl(), ssbTrack.getUser()),
            ssbTrack.getIsPrivacy(),
            ssbTrack.getCreatedDateTime(),
            new UserProfileRepDto(ssbTrack.getUser())
        );
    }

    public static TrackPlayRepDto create(SsbTrack ssbTrack) {
        // 서버 호출 시간

        return new TrackPlayRepDto(ssbTrack);
    }
    public static void updateTrackPlayLogRepDto(TrackPlayRepDto trackPlayRepDto,
        TrackPlayLogRepDto trackPlayLogRepDto) {
        // 조회수 측정 정보
        trackPlayRepDto.setTrackPlayLogRepDto(trackPlayLogRepDto);
    }
}
