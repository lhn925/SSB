package sky.Sss.domain.user.utili.jwt;


import lombok.Getter;
import lombok.Setter;
import sky.Sss.global.redis.dto.RedisKeyDto;

@Getter
@Setter
public class JwtTokenDto {

    private String accessToken;
    private String refreshToken;
    private String redisToken;

    public void setAccessToken(String accessToken) {
        this.accessToken = "Bearer " + accessToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = "Bearer " + refreshToken;
    }
    public void setRedisToken(String redisToken) {
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
