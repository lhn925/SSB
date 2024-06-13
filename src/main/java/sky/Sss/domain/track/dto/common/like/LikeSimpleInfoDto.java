package sky.Sss.domain.track.dto.common.like;


import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.user.dto.UserSimpleInfoDto;
import sky.Sss.domain.user.entity.User;

@NoArgsConstructor(access = PROTECTED)
@Getter
@Setter(PRIVATE)
public class LikeSimpleInfoDto {


    private String token;
    private UserSimpleInfoDto userSimpleInfoDto;

    public LikeSimpleInfoDto(String token, User user) {
        this.token = token;
        this.userSimpleInfoDto = new UserSimpleInfoDto(user);
    }
}
