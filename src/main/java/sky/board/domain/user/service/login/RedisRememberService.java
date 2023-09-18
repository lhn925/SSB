package sky.board.domain.user.service.login;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;
import org.springframework.util.StringUtils;
import sky.board.domain.user.dto.login.CustomUserDetails;
import sky.board.domain.user.model.RememberCookie;
import sky.board.domain.user.utili.CustomCookie;
import sky.board.domain.user.utili.UserTokenUtil;
import sky.board.global.redis.dto.RedisKeyDto;
import sky.board.global.redis.service.RedisService;

@Slf4j
public class RedisRememberService extends AbstractRememberMeServices {

    private final RedisService redisService;
    private final UserLoginStatusService userLoginStatusService;

    private String token;

    public RedisRememberService(String key, UserDetailsService userDetailsService, RedisService redisService,UserLoginStatusService userLoginStatusService) {
        super(key, userDetailsService);
        super.setParameter(key);
        super.setCookieName(key);
        this.redisService = redisService;
        this.userLoginStatusService = userLoginStatusService;
    }

    @Override
    public void onLoginSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication successfulAuthentication) {
        String userId = retrieveUserId(successfulAuthentication);
        String password = retrievePassword(successfulAuthentication);

        if (!StringUtils.hasText(userId)) {
            log.debug("userId가 존재하지 않음  " + userId);
            return;
        }
        if (!StringUtils.hasText(password)) {
            UserDetails user = getUserDetailsService().loadUserByUsername(userId);
            password = user.getPassword();
            if (!StringUtils.hasLength(password)) {
                log.debug("password가 존재하지 않음  " + userId);
                return;
            }
        }
        HttpSession session = request.getSession();

        int tokenLifetime = getTokenValiditySeconds(); // 2주
        long expiryTime = getExpiryTime(tokenLifetime);
        String token = UserTokenUtil.getToken();
        this.setToken(token);
        // 쿠키 생성
        setCookie(null, tokenLifetime, request, response);
        // 레디스 생성
        setRedis(userId, session, expiryTime, token);
    }

    public long getExpiryTime(int tokenLifetime) {
        long expiryTime = System.currentTimeMillis();
        expiryTime += 1000L * ((tokenLifetime < 0) ? TWO_WEEKS_S : tokenLifetime);
        return expiryTime;
    }


    @Override
    protected void setCookie(String[] tokens, int maxAge, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();

        // 기존에 있던 rememberCookie삭제
        Cookie rmCookie = CustomCookie.getCookie(request.getCookies(), RememberCookie.KEY.getValue());
        CustomCookie.delete(rmCookie, response);

        String value = session.getId() + ":" + this.getToken();
        Cookie cookie = new Cookie(super.getCookieName(), value);
        cookie.setPath(request.getContextPath());
        cookie.setMaxAge(maxAge);
        cookie.setDomain(request.getContextPath());
        cookie.setHttpOnly(true);

        // LoginStatus rememberMe key 저장
        request.setAttribute(RememberCookie.KEY.getValue(), hashing(value));
        response.addCookie(cookie);
    }


    public void publicSetCookie(int maxAge, HttpServletRequest request, HttpServletResponse response,
        String token) {
        setToken(token);
        this.setCookie(null, maxAge, request, response);
    }

    public void setRedis(String userId, HttpSession session, long expiryTime, String token) {
        String redisKey = UserTokenUtil.hashing(session.getId().getBytes(), token);
        JSONArray userArray = new JSONArray();
        userArray.add("userId:" + userId);
        userArray.add("expireTime:" + expiryTime);
        redisService.setRememberData(redisKey, userArray.toString(), expiryTime);
    }


    public String getToken() {
        return token;
    }

    private void setToken(String token) {
        this.token = token;
    }

    /**
     * 쿠키가 있을시에
     *
     * @param cookieTokens
     *     the decoded and tokenized cookie value
     * @param request
     *     the request
     * @param response
     *     the response, to allow the cookie to be modified if required.
     * @return
     * @throws RememberMeAuthenticationException
     * @throws UsernameNotFoundException
     */
    @Override
    protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request,
        HttpServletResponse response) throws RememberMeAuthenticationException, UsernameNotFoundException {

        if (cookieTokens.length == 0) {
            throw new RuntimeException();
        }
        UserDetails user = getUserDetailsService().loadUserByUsername(cookieTokens[0]);

        return user;
    }


    @Override
    protected String[] decodeCookie(String rememberMeCookie) throws InvalidCookieException {
        String redisData = getRedisData(rememberMeCookie);
        try {

            JSONObject userObject = getJsonObject(redisData);

            String[] strings = new String[]{(String) userObject.get("userId")};
            return strings;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public JSONObject getJsonObject(String redisData) throws JsonProcessingException {
        if (!StringUtils.hasText(redisData)) {
            throw new InvalidCookieException("Invalid remember-me cookie:");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> userInfo = objectMapper.readValue(redisData, ArrayList.class);
        JSONObject userObject = new JSONObject();

        userInfo.stream().forEach(u ->
            {
                String[] list = u.split(":");
                String key = list[0];
                String value = list[1];

                if (key == null || !StringUtils.hasText(key)) {
                    throw new UsernameNotFoundException("key Not Found");
                }
                if (value == null || !StringUtils.hasText(value)) {
                    throw new UsernameNotFoundException("value Not Found");
                }
                userObject.put(key, value);
            }
        );
        return userObject;
    }

    public String getRedisData(String rememberMeCookie) {
        rememberMeCookie = hashing(rememberMeCookie);

        String redisData = redisService.getRememberData(rememberMeCookie);
        return redisData;
    }

    /**
     * 쿠키 값 해석
     *
     * @param rememberMeCookie
     * @return
     */
    public String hashing(String rememberMeCookie) {
        String[] cookie = rememberMeCookie.split(":");
        rememberMeCookie = UserTokenUtil.hashing(cookie[0].getBytes(), cookie[1]);
        return rememberMeCookie;
    }


    /**
     * 아이디 가져오는 메서드
     *
     * @param authentication
     * @return
     */
    protected String retrieveUserId(Authentication authentication) {
        if (isInstanceOfUserDetails(authentication)) {
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        }
        return authentication.getPrincipal().toString();
    }

    /**
     * 패스워드
     *
     * @param authentication
     * @return
     */
    protected String retrievePassword(Authentication authentication) {
        if (isInstanceOfUserDetails(authentication)) {
            return ((UserDetails) authentication.getPrincipal()).getPassword();
        }
        if (authentication.getCredentials() != null) {
            return authentication.getCredentials().toString();
        }
        return null;
    }

    /**
     * UserDetail 확인 메서드
     *
     * @param authentication
     * @return
     */
    private boolean isInstanceOfUserDetails(Authentication authentication) {

        return authentication.getPrincipal() instanceof CustomUserDetails;
    }


    public RedisService getRedisService() {
        return redisService;
    }

    public UserLoginStatusService getUserLoginStatusService() {
        return userLoginStatusService;
    }
}
