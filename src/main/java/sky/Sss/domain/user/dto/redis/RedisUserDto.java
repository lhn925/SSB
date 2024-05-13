package sky.Sss.domain.user.dto.redis;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.PwSecLevel;
import sky.Sss.domain.user.model.UserGrade;

@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
public class RedisUserDto {

    private Long id;
    private String userId;
    private String userName;
    private String email;
    private String token;
    private String pictureUrl;
    private LocalDateTime userNameModifiedDate;
    private UserGrade grade;
    private PwSecLevel pwSecLevel;
    private Boolean isEnabled;
    private Boolean isLoginBlocked;
    private LocalDateTime lastModifiedDateTime;
    private LocalDateTime createdDateTime;

    @Builder
    private RedisUserDto(Long id, String userId, String userName, String email, String pictureUrl,
        LocalDateTime userNameModifiedDate, UserGrade grade, PwSecLevel pwSecLevel, Boolean isEnabled,
        Boolean isLoginBlocked, LocalDateTime lastModifiedDateTime, LocalDateTime createdDateTime, String token) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.pictureUrl = pictureUrl;
        this.userNameModifiedDate = userNameModifiedDate;
        this.grade = grade;
        this.pwSecLevel = pwSecLevel;
        this.isEnabled = isEnabled;
        this.isLoginBlocked = isLoginBlocked;
        this.lastModifiedDateTime = lastModifiedDateTime;
        this.createdDateTime = createdDateTime;
        this.token = token;
    }

    public static RedisUserDto create(User user) {
        return RedisUserDto.builder().
            id(user.getId()).
            token(user.getToken()).
            userName(user.getUserName()).userId(user.getUserId())
            .email(user.getEmail())
            .pictureUrl(user.getPictureUrl()).userNameModifiedDate(user.getUserNameModifiedDate())
            .grade(user.getGrade()).pwSecLevel(user.getPwSecLevel()).isEnabled(user.getIsEnabled())
            .isLoginBlocked(user.getIsLoginBlocked()).lastModifiedDateTime(user.getLastModifiedDateTime())
            .createdDateTime(user.getCreatedDateTime()).build();

    }


}
