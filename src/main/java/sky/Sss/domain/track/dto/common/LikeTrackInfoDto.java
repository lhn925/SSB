package sky.Sss.domain.track.dto.common;


import static lombok.AccessLevel.PROTECTED;

import lombok.Getter;
import lombok.NoArgsConstructor;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.user.entity.User;

@NoArgsConstructor(access = PROTECTED)
@Getter
public class LikeTrackInfoDto extends LikeTargetInfoDto {

    private SsbTrack ssbTrack;
    public LikeTrackInfoDto(Long targetId, String targetToken, String targetContents,
        User fromUser, SsbTrack ssbTrack) {
        super(targetId, targetToken, targetContents, fromUser);
        this.ssbTrack = ssbTrack;
    }
}
