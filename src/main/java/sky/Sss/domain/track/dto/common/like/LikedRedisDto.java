package sky.Sss.domain.track.dto.common.like;


import java.time.LocalDateTime;
import java.util.Comparator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(value = AccessLevel.PRIVATE)
@NoArgsConstructor
public class LikedRedisDto {

    private Long id;
    private Long targetId;
    private Long uid;
    private LocalDateTime createdDateTime;


    public LikedRedisDto(Long id, Long targetId, Long uid, LocalDateTime createdDateTime) {
        this.id = id;
        this.targetId = targetId;
        this.uid = uid;
        this.createdDateTime = createdDateTime;
    }
    public static final Comparator<LikedRedisDto> BY_ID_ASCENDING = Comparator.comparing(LikedRedisDto::getId);
}
