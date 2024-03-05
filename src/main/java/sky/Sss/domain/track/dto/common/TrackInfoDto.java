package sky.Sss.domain.track.dto.common;


import static lombok.AccessLevel.PROTECTED;

import lombok.Getter;
import lombok.NoArgsConstructor;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.user.entity.User;

@NoArgsConstructor(access = PROTECTED)
@Getter
public class TrackInfoDto extends TargetInfoDto {

    private SsbTrack ssbTrack;
    public TrackInfoDto(Long targetId, String targetToken, String targetContents,
        User fromUser, SsbTrack ssbTrack,Boolean isPrivacy) {
        super(targetId, targetToken, targetContents, fromUser,isPrivacy);
        this.ssbTrack = ssbTrack;
    }
}
