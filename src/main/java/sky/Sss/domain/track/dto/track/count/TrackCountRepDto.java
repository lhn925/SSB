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



    public static TrackCountRepDto create(SsbChartIncludedPlays ssbChartIncludedPlays) {
        TrackCountRepDto trackCountRepDto = new TrackCountRepDto();

        trackCountRepDto.setId(ssbChartIncludedPlays.getId());
        return trackCountRepDto;
    }



}
