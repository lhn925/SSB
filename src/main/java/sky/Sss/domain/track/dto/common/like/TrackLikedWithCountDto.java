package sky.Sss.domain.track.dto.common.like;


import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.dto.track.rep.TrackDetailDto;

@Getter
@Setter(value = AccessLevel.PRIVATE)
@NoArgsConstructor(access = PROTECTED)
public class TrackLikedWithCountDto {
    // trackLike

    private List<TrackInfo> targetInfos = new ArrayList<>();
    private Integer totalCount;

    public TrackLikedWithCountDto(int totalCount) {
        this.totalCount = totalCount;
    }

    public void addTarget(long id, TrackDetailDto trackDetailDto, LocalDateTime createdDateTime) {
        this.targetInfos.add(new TrackInfo(id, trackDetailDto, createdDateTime));
    }

    @Getter
    @Setter(PRIVATE)
    public static class TrackInfo {

        private Long id;

        private TrackDetailDto detail;

        private LocalDateTime createdDateTime;

        public TrackInfo(long id, TrackDetailDto trackInfo, LocalDateTime createdDateTime) {
            this.id = id;
            this.detail = trackInfo;
            this.createdDateTime = createdDateTime;
        }
    }
}
