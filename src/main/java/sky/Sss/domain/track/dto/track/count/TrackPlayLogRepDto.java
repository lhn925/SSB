package sky.Sss.domain.track.dto.track.count;


// track 조회수 테이블에 Dto

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.entity.chart.SsbChartIncludedPlays;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrackCountRepDto {

    private Long id;

    private String token;

    private Long startTime;

    private Integer miniNumPlayTime;

    // 현재시간대 에서 처음 플레이 한건지 판단 여부
    // true 면 처음부터 끝까지 들었을 경우 공식플레이로 집계
    private Boolean isFirst;



    public static TrackCountRepDto create(SsbChartIncludedPlays ssbChartIncludedPlays) {
        TrackCountRepDto trackCountRepDto = new TrackCountRepDto();

        trackCountRepDto.setId(ssbChartIncludedPlays.getId());
        return trackCountRepDto;
    }



}
