package sky.Sss.domain.track.dto.common;


import static lombok.AccessLevel.PROTECTED;

import lombok.Getter;
import lombok.NoArgsConstructor;
import sky.Sss.domain.track.entity.playList.SsbPlayListSettings;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.user.entity.User;

@NoArgsConstructor(access = PROTECTED)
@Getter
public class LikePlyInfoDto extends LikeTargetInfoDto {

    private SsbPlayListSettings ssbPlayListSettings;
    public LikePlyInfoDto(Long targetId, String targetToken, String targetContents,
        User fromUser, SsbPlayListSettings ssbPlayListSettings) {
        super(targetId, targetToken, targetContents, fromUser);
        this.ssbPlayListSettings = ssbPlayListSettings;
    }
}
