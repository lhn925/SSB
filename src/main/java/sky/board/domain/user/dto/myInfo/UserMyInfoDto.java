package sky.board.domain.user.dto.myInfo;


import java.io.Serializable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.board.domain.user.dto.UserInfoDto;


@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserMyInfoDto implements Serializable {

    private String userId;
    private String email;
    private String userName;
    private String pictureUrl;

    @Builder
    private UserMyInfoDto(String userId, String email, String userName, String pictureUrl) {
        this.userId = userId;
        this.email = email;
        this.userName = userName;
        this.pictureUrl = pictureUrl;
    }


    public static UserMyInfoDto createUserMyInfo(UserInfoDto userInfoDto) {
        return UserMyInfoDto.builder()
            .userId(userInfoDto.getUserId())
            .email(userInfoDto.getEmail())
            .pictureUrl(userInfoDto.getPictureUrl())
            .userName(userInfoDto.getUserName()).build();
    }


}
