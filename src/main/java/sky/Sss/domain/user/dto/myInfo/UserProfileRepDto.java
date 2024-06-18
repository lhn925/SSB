package sky.Sss.domain.user.dto.myInfo;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.user.entity.User;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
public class UserProfileRepDto {
    private Long id;
    public UserProfileRepDto(User user) {
        this.id = user.getId();
    }
}
