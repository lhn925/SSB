package sky.Sss.domain.user.dto.login;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import sky.Sss.global.redis.dto.RedisKeyDto;

@Getter
@Setter(AccessLevel.PRIVATE)
public class LoginSuccessTokenDto {

    private Long id;
    private String accessToken;
    private String refreshToken;

    private void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    private void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public static LoginSuccessTokenDto createJwtTokenDto (Long id,String accessToken,String refreshToken) {
        LoginSuccessTokenDto jwtTokenDto = new LoginSuccessTokenDto();
        jwtTokenDto.setId(id);

        jwtTokenDto.setAccessToken(accessToken);
        jwtTokenDto.setRefreshToken(refreshToken);
        return jwtTokenDto;
    }
}
