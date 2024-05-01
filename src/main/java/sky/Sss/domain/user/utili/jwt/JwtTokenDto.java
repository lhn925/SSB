package sky.Sss.domain.user.utili.jwt;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import sky.Sss.global.redis.dto.RedisKeyDto;

@Getter
@Setter(AccessLevel.PRIVATE)
public class JwtTokenDto {

    private String accessToken;
    private String refreshToken;
    private String redisToken;

    private void setAccessToken(String accessToken) {
        this.accessToken = "Bearer " + accessToken;
    }

    private void setRefreshToken(String refreshToken) {
        this.refreshToken = "Bearer " + refreshToken;
    }
    private void setRedisToken(String redisToken) {
        this.redisToken = RedisKeyDto.REDIS_LOGIN_KEY + redisToken;
    }
    public static JwtTokenDto createJwtTokenDto (String redisToken,String accessToken,String refreshToken) {
        JwtTokenDto jwtTokenDto = new JwtTokenDto();
        jwtTokenDto.setRedisToken(redisToken);
        jwtTokenDto.setAccessToken(accessToken);
        jwtTokenDto.setRefreshToken(refreshToken);
        return jwtTokenDto;
    }
}
