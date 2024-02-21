package sky.Sss.domain.user.utili.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import sky.Sss.domain.user.entity.login.UserLoginStatus;
import sky.Sss.domain.user.exception.RefreshTokenNotFoundException;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.service.login.UserLoginStatusService;
import sky.Sss.domain.user.utili.TokenUtil;
import sky.Sss.global.redis.dto.RedisKeyDto;
import sky.Sss.global.redis.service.RedisQueryService;

//추가된 라이브러리를 사용해서 JWT를 생성하고 검증하는 컴포넌트
@Slf4j
@Component
public class TokenProvider implements InitializingBean {


    private final RedisQueryService redisQueryService;
    public static final String AUTHORITIES_KEY = "auth";
    public static final String REDIS_TOKEN_KEY = "redis";
    private final String accessSecret;
    private final String refreshSecret;
    private final UserLoginStatusService userLoginStatusService;
    private final long tokenValidityInMilliseconds;
    private final long tokenValidityOneHourInSeconds;
    private Key accessKey;
    private Key refreshKey;


    // 시크릿키 및 시간 초기화
    public TokenProvider(@Value("${jwt.accessSecret}") String accessSecret,
        @Value("${jwt.refreshSecret}") String refreshSecret,
        @Value("${jwt.token-validity-in-seconds}") long tokenValidityInMilliseconds,
        @Value("${jwt.token-validity-in-one-Hour-seconds}") long tokenValidityOneHourInSeconds,
        RedisQueryService redisQueryService, UserLoginStatusService userLoginStatusService) {
        this.accessSecret = accessSecret;
        this.refreshSecret = refreshSecret;
        // 14일 refreshToken 발급
        this.tokenValidityInMilliseconds = tokenValidityInMilliseconds * 14000;
        // 1 시간 accessToken 발급
        this.tokenValidityOneHourInSeconds = tokenValidityOneHourInSeconds * 60;
        this.redisQueryService = redisQueryService;
        this.userLoginStatusService = userLoginStatusService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 각 secret 변수를 Base64 디코드한후 key 변수에 삽입
        byte[] accessKeyBytes = Decoders.BASE64.decode(this.accessSecret);
        byte[] refreshKeyBytes = Decoders.BASE64.decode(this.refreshSecret);
        this.accessKey = Keys.hmacShaKeyFor(accessKeyBytes);
        this.refreshKey = Keys.hmacShaKeyFor(refreshKeyBytes);
    }


    // 로그인시 accessToken 및 refreshToken 생성
    // 토큰 생성 및 설정
    public JwtTokenDto createToken(Authentication authentication) {

        //레디스 토큰값 생성
        String redisToken = TokenUtil.getToken();
        // expire 시간 설정
        long now = (new Date()).getTime();

        String accessToken = createAccessToken(authentication, redisToken, now);

        String refreshToken = createRefreshToken(authentication, redisToken, now);

        JwtTokenDto jwtTokenDto = JwtTokenDto.createJwtTokenDto(redisToken, accessToken, refreshToken);

        // redisToken redis 에 유효기간 14일 저장
        redisQueryService.setData(jwtTokenDto.getRedisToken(), authentication.getName(),
            now + this.tokenValidityInMilliseconds);
        return jwtTokenDto;
    }

    private String getAuthorities(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));
        return authorities;
    }

    public String createAccessToken(Authentication authentication, String redisToken, long now) {
        Date accessValidity = new Date(now + this.tokenValidityOneHourInSeconds);
        String authorities = getAuthorities(authentication);
        // accessToken 생성
        String accessToken = createToken(Jwts.builder()
            .setSubject(authentication.getName())
            .setIssuedAt(new Date(now)), authorities, redisToken, accessKey, accessValidity);
        return accessToken;
    }

    public String createRefreshToken(Authentication authentication, String redisToken, long now) {
        Date refreshValidity = new Date(now + this.tokenValidityInMilliseconds);
        String authorities = getAuthorities(authentication);
        // refreshToken 생성
        String refreshToken = createToken(Jwts.builder()
            .setIssuedAt(new Date(now)) // 토큰 발행시간
            .setSubject(authentication.getName()), authorities, redisToken, refreshKey, refreshValidity);
        return refreshToken;
    }

    // 토큰으로 payload 만들고 이를 이용해 유저 객체를 만들어서 최종적으로 authentication 객체를 리턴
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts
            .parserBuilder()
            .setSigningKey(accessKey)
            .build()
            .parseClaimsJws(token)
            .getBody();
        Collection<? extends GrantedAuthority> authorities =
            Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        log.info("claims = {}", claims);
        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    // access 토큰의 유효성 검증을 수행
    public JSONObject validateAccessToken(String token) {
        JSONObject json = new JSONObject();
        Boolean isSuccess = false;
        try {
            Jws<Claims> claimsJws = getAccessClaimsJws(token);

            String redisToken = (String) claimsJws.getBody().get(REDIS_TOKEN_KEY);
            // 레디스에 없을 경우 유효한 jwt 토큰으로 판단 x
            Boolean isRedis = hasRedisToken(redisToken);
            if (!isRedis) {
                throw new RefreshTokenNotFoundException();
            }
            json.put("success", !isSuccess);
            return json;
        } catch (RefreshTokenNotFoundException | ExpiredJwtException e) {
            log.info("e = {}", token);
        } catch (JwtException | IllegalArgumentException e) {
            log.info("e = {}", token);
        }

        json.put("success", isSuccess);
        return json;
    }

    // refresh 토큰의 유효성 검증을 수행
    public String validateRefreshToken(String refreshToken) {
        try {
            String Token = resolveToken(refreshToken);
            Jws<Claims> claimsJws = getRefreshClaimsJws(Token);

            String redisToken = (String) claimsJws.getBody().get(REDIS_TOKEN_KEY);
            // 레디스에 없을 경우 유효한 jwt 토큰으로 판단 x

            String sub = (String) claimsJws.getBody().get("sub");

            // 전달된 토큰 존재 여부 확인
            Boolean isToken = StringUtils.hasText(redisToken);

            tokenValidate(refreshToken, redisToken, sub, isToken);
            //accessToken 재발급
            return recreationAccessToken(claimsJws);
        } catch (RefreshTokenNotFoundException | ExpiredJwtException e) {
            log.info("e = {}", refreshToken);
        } catch (JwtException | IllegalArgumentException e) {
            log.info("e = {}", refreshToken);
        }
        return null;
    }




    // 레디스 토큰 자체가 전달이 안 되어있을 경우 -> refreshToken으로 find 있으면 off 없으면 exception
    // 레디스토큰이 레디스에는 없고 디비에만 있을 경우 -> 활성화 off
    // 디비에는 없고 레디스에만 있을경우 -> 레디스 토큰 삭제
    // 레디스에 userId와 전달된 id가 다를 경우
    private void tokenValidate(String refreshToken, String redisToken, String sub, Boolean isToken) {

        log.info("isToken = {}", isToken);
        if (!isToken) { // 토큰이 아예 전달이 안된 경우
            userLoginStatusService.update(sub, refreshToken, Status.OFF, Status.OFF);
            throw new RefreshTokenNotFoundException();
        }

        // 레디스 존재 여부 확인
        Boolean isRedis = hasRedisToken(redisToken);
        redisToken = RedisKeyDto.REDIS_LOGIN_KEY + redisToken;
        // 전달은 됐지만 레디스에 토큰이 없는 경우
        log.info("isRedis = {}", isRedis);
        if (!isRedis) {
            userLoginStatusService.update(sub,
                redisToken, refreshToken, Status.OFF, Status.OFF);
            throw new RefreshTokenNotFoundException();
        }

        // 레디스에 저장한 아이디와 전달한 아이디가 서로 다른 경우
        String userId = redisQueryService.getData(redisToken);
        log.info("userId = {}", userId);
        log.info("sub = {}", sub);
        if (!sub.equals(userId)) {
            throw new RefreshTokenNotFoundException();
        }

        // DB 존재 여부 확인
        // 레디스에는 있지만 디비에는 없는 경우
        // 레디스에서 삭제

        UserLoginStatus findStatus = userLoginStatusService.findOne(sub, redisToken,
            refreshToken, Status.ON, Status.ON);
        log.info("findStatus = {}", findStatus);

        if (findStatus == null) {
            redisQueryService.delete(redisToken);
            throw new RefreshTokenNotFoundException();
        }
    }

    public String resolveToken(String token) {
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            throw new AuthenticationServiceException("error.token");
        }
        return token;
    }

    private Boolean hasRedisToken(String redisToken) {
        Boolean result = redisQueryService.hasRedis(RedisKeyDto.REDIS_LOGIN_KEY + redisToken);
        return result;
    }

    /**
     * accessToken 재발급
     *
     * @param claimsJws
     * @return
     */
    public String recreationAccessToken(Jws<Claims> claimsJws) {
        long now = (new Date()).getTime();
        Date accessValidity = new Date(now + this.tokenValidityOneHourInSeconds);
        Date nowDate = new Date(now);
        Object auth = claimsJws.getBody().get(AUTHORITIES_KEY);
        Object redis = claimsJws.getBody().get(REDIS_TOKEN_KEY);
        Key accessKey = this.accessKey;
        String sub = (String) claimsJws.getBody().get("sub");

        // accessToken 생성
        String accessToken = createToken(Jwts.builder()
            .setSubject(sub)
            .setIssuedAt(nowDate), auth, redis, accessKey, accessValidity);
        return accessToken;
    }

    private static String createToken(JwtBuilder sub, Object auth, Object redis, Key accessKey,
        Date accessValidity) {
        String accessToken = sub // 토큰 발행시간
            .claim(AUTHORITIES_KEY, auth) // payload 정보저장
            .claim(REDIS_TOKEN_KEY, redis) // payload 정보저장
            .signWith(accessKey, SignatureAlgorithm.HS512)// 서명 암호화 알고리즘 signature 에 들어갈 secret값 세팅
            .setExpiration(accessValidity) // expire
            .compact();
        return accessToken;
    }


    public Jws<Claims> getRefreshClaimsJws(String refreshToken) {
        Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(this.refreshKey).build()
            .parseClaimsJws(refreshToken);
        return claimsJws;
    }

    public Jws<Claims> getAccessClaimsJws(String accessToken) {
        Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(this.accessKey).build().parseClaimsJws(accessToken);
        return claimsJws;
    }

    public long getTokenValidityInMilliseconds() {
        return tokenValidityInMilliseconds;
    }
}
