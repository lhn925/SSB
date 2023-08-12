package sky.board.domain.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;
import org.springframework.util.StringUtils;
import sky.board.domain.user.utill.UserTokenUtil;
import sky.board.global.redis.service.RedisService;

@Slf4j
public class RedisRememberService extends AbstractRememberMeServices {

    /*
        public static final String SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY = "rememberMe";

        public static final String DEFAULT_PARAMETER = "rememberMe";

        public static final int TWO_WEEKS_S = 1209600; // 2주

        private String cookieName = SPRING_SECURITY_REMEMBER_ME_COOKIE_KEY;

        private String cookieDomain;

        private String parameter = DEFAULT_PARAMETER;
        private String key = DEFAULT_PARAMETER;

        private UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();
        private int tokenValiditySeconds = TWO_WEEKS_S;

        private final AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();

        private final RedisService redisService;
        private final UserDetailsService userDetailsService;
        private final ObjectMapper objectMapper;
    */
    private final RedisService redisService;
    private String token;

    public RedisRememberService(String key, UserDetailsService userDetailsService, RedisService redisService) {
        super(key, userDetailsService);
        super.setParameter(key);
        super.setCookieName(key);
        super.setCookieDomain("/");
        this.redisService = redisService;
    }

/*

    @Override
    protected void onLoginSuccess(HttpServletRequest request, HttpServletResponse response) {
        log.info("RedisRememberService autoLogin");

        String rememberMeCookie = extractRememberMeCookie(request);
        if (rememberMeCookie == null) {
            return null;
        }
        if (!StringUtils.hasText(rememberMeCookie)) {
            return null;
        }

        log.info("rememberMeCookie = {}", rememberMeCookie);

        String[] cookie = rememberMeCookie.split(":");
        rememberMeCookie = UserTokenUtil.hashing(cookie[0].getBytes(), cookie[1]);

        String redisData = redisService.getData(rememberMeCookie);
        try {
            List<String> userInfo = objectMapper.readValue(redisData, ArrayList.class);
            JSONObject userObject = new JSONObject();
            userInfo.stream().forEach(u ->
                {
                    log.info("u = {}", u);
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

            UserDetails user = userDetailsService.loadUserByUsername((String) userObject.get("userId"));

            this.userDetailsChecker.check(user);
            Authentication successfulAuthentication = createSuccessfulAuthentication(request, user);

            log.info("successfulAuthentication.getPrincipal() = {}", successfulAuthentication.getPrincipal());
            log.info("successfulAuthentication.getPrincipal() = {}", successfulAuthentication.getAuthorities());


            return successfulAuthentication;

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
*/


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
                log.debug("userId가 존재하지 않음  " + userId);
                return;
            }
        }
        HttpSession session = request.getSession();

        int tokenLifetime = getTokenValiditySeconds(); // 2주
        long expiryTime = System.currentTimeMillis();
        log.info("expiryTime = {}", expiryTime);
        expiryTime += 1000L * ((tokenLifetime < 0) ? TWO_WEEKS_S : tokenLifetime);
        String token = UserTokenUtil.getToken();
        this.setToken(token);
        // 쿠키 생성
        setCookie(null, tokenLifetime, request, response);

        // 레디스 생성
        setRedis(userId, password, session, expiryTime, token);
    }


    @Override
    protected void setCookie(String[] tokens, int maxAge, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        Cookie cookie = new Cookie(super.getCookieName(), session.getId() + ":" + this.getToken());
        cookie.setPath(request.getContextPath());
        cookie.setMaxAge(maxAge);
        cookie.setDomain(request.getContextPath());
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    private void setRedis(String userId, String password, HttpSession session, long expiryTime, String token) {
        String redisKey = UserTokenUtil.hashing(session.getId().getBytes(), token);
        JSONArray userArray = new JSONArray();
        userArray.add("userId:" + userId);
        userArray.add("password:" + password);
        userArray.add("expireTime:" + expiryTime);
        log.info("userArray.toString() = {}", userArray.toArray().toString());
        redisService.setData(redisKey, userArray.toString(), expiryTime);
    }



    public String getToken() {
        return token;
    }

    private void setToken(String token) {
        this.token = token;
    }

    @Override
    protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request,
        HttpServletResponse response) throws RememberMeAuthenticationException, UsernameNotFoundException {
        UserDetails user = getUserDetailsService().loadUserByUsername(cookieTokens.toString());
        return user;
    }

    @Override
    protected String[] decodeCookie(String rememberMeCookie) throws InvalidCookieException {
        String[] cookie = rememberMeCookie.split(":");
        rememberMeCookie = UserTokenUtil.hashing(cookie[0].getBytes(), cookie[1]);

        String redisData = redisService.getData(rememberMeCookie);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<String> userInfo = objectMapper.readValue(redisData, ArrayList.class);
            JSONObject userObject = new JSONObject();

            userInfo.stream().forEach(u ->
                {
                    log.info("u = {}", u);
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
            String[] strings = StringUtils.addStringToArray(new String[0], (String) userObject.get("userId"));
            return strings;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
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
        return authentication.getPrincipal() instanceof UserDetails;
    }
}
