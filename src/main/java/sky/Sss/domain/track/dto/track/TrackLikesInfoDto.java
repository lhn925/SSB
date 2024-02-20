package sky.Sss.domain.track.dto.track;


import static lombok.AccessLevel.PROTECTED;

import java.io.Serializable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.user.entity.User;


@Setter
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
public class TrackLikesInfoDto implements Serializable {

    private User user;
    private Long trackId;

}
