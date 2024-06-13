package sky.Sss.domain.track.dto.common.repost;


import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.entity.SsbRepost;
import sky.Sss.domain.user.dto.UserSimpleInfoDto;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.ContentsType;

@NoArgsConstructor(access = PROTECTED)
@Getter
@Setter(PRIVATE)
public class RepostSimpleInfoDto {


    private RepostRedisDto repostSimpleInfo;
    private UserSimpleInfoDto userSimpleInfoDto;

    public RepostSimpleInfoDto(SsbRepost ssbRepost, User user,String targetToken) {
        this.repostSimpleInfo = new RepostRedisDto(ssbRepost,targetToken);
        this.userSimpleInfoDto = new UserSimpleInfoDto(user);
    }
}
