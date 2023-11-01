package sky.Sss.domain.user.service.login;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.entity.login.UserLoginStatus;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.domain.user.utili.jwt.TokenProvider;
import sky.Sss.global.redis.dto.RedisKeyDto;
import sky.Sss.global.redis.service.RedisService;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LogoutService {


    private final TokenProvider tokenProvider;
    private final UserQueryService userQueryService;
    private final RedisService redisService;
    private final UserLoginStatusService userLoginStatusService;

    @Transactional
    public void logout(String accessToken, String refreshToken) throws AuthenticationException {

        Jws<Claims> accessClaimsJws = tokenProvider.getAccessClaimsJws(accessToken);
        Jws<Claims> refreshClaimsJws = tokenProvider.getRefreshClaimsJws(refreshToken);
        Claims accessBody = accessClaimsJws.getBody();
        Claims refreshBody = refreshClaimsJws.getBody();

        String accessUserId = (String) accessBody.get("sub");
        String refreshUserId = (String) refreshBody.get("sub");

        log.info("refreshUserId = {}", refreshUserId);
        log.info("accessUserId = {}", accessUserId);


        String redisToken = RedisKeyDto.REDIS_LOGIN_KEY + accessBody.get("redis");
        if (!accessUserId.equals(refreshUserId)) {
            throw new BadCredentialsException("error");
        }
        User findUser = userQueryService.findOne(refreshUserId);

        // status 종료
        UserLoginStatus findStatus = userLoginStatusService.update(findUser.getUserId(),
             redisToken, "Bearer "+refreshToken,
            Status.OFF, Status.OFF);

        // 레디스 삭제
        if (findStatus != null || redisService.hasRedis(redisToken)) {
            redisService.delete(redisToken);
        }
    }
}
