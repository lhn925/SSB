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

    public void addTarget(long id, long trackId, LocalDateTime createdDateTime) {
        this.targetInfos.add(new TrackInfo(id, trackId, createdDateTime));
    }

    @Getter
    @Setter(PRIVATE)
    public static class TrackInfo {

        private Long likeId;

        private Long trackId;

        private LocalDateTime createdDateTime;

        public TrackInfo(long likeId, long trackId, LocalDateTime createdDateTime) {
            this.likeId = likeId;
            this.trackId = trackId;
            this.createdDateTime = createdDateTime;
        }
    }
}
