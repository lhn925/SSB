package sky.Sss.domain.user.dto.myInfo;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.dto.track.TrackInfoSimpleDto;
import sky.Sss.domain.user.entity.User;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
public class UserProfileRepDto {
    private Long id;
    private String userName;
    private Boolean isFollow;


    public UserProfileRepDto(User user) {
        this.id = user.getId();
        this.userName = user.getUserName();
    }

    public static void updateIsFollow(UserProfileRepDto userProfileRepDto, boolean isFollow) {
        userProfileRepDto.setIsFollow(isFollow);
    }
}
