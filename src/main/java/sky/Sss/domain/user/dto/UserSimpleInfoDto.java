package sky.Sss.domain.user.dto;


import static lombok.AccessLevel.PRIVATE;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.user.entity.User;

@Getter
@Setter(value = PRIVATE)
@NoArgsConstructor
public class UserSimpleInfoDto {

    private Long id;
    private String userId;
    private String token;
//    private String userName;
//    private String pictureUrl;

    public UserSimpleInfoDto(User user) {
        this.id = user.getId();
        this.userId = user.getUserId();
        this.token = user.getToken();
    }
}
