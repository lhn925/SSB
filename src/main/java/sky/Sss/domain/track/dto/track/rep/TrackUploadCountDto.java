package sky.Sss.domain.track.dto.track.rep;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.user.entity.User;

@Getter
@Setter(value = AccessLevel.PRIVATE)
@NoArgsConstructor
public class TrackUploadCountDto {

    private Long uid;
    private Long totalCount;


    public TrackUploadCountDto(long uid, long totalCount) {
        this.uid = uid;
        this.totalCount = totalCount;
    }

}
