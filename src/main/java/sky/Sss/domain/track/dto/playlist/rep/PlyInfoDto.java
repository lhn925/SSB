package sky.Sss.domain.track.dto.playlist.rep;


import static lombok.AccessLevel.PROTECTED;

import lombok.Getter;
import lombok.NoArgsConstructor;
import sky.Sss.domain.track.dto.common.rep.TargetInfoDto;
import sky.Sss.domain.track.entity.playList.SsbPlayListSettings;
import sky.Sss.domain.user.entity.User;

@NoArgsConstructor(access = PROTECTED)
@Getter
public class PlyInfoDto extends TargetInfoDto {

    private SsbPlayListSettings ssbPlayListSettings;
    public PlyInfoDto(Long targetId, String targetToken, String targetContents,
        User fromUser, SsbPlayListSettings ssbPlayListSettings,Boolean isPrivacy) {
        super(targetId, targetToken, targetContents, fromUser,isPrivacy);
        this.ssbPlayListSettings = ssbPlayListSettings;
    }
}
