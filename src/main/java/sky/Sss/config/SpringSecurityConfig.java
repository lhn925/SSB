package sky.Sss.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import sky.Sss.domain.user.utili.jwt.JwtAccessDeniedHandler;
import sky.Sss.domain.user.utili.jwt.JwtAuthenticationEntryPoint;
import sky.Sss.domain.user.utili.jwt.TokenProvider;
import sky.Sss.global.redis.service.RedisQueryService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SpringSecurityConfig {


    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    private final String[] ALL_URL = {"/", "/js/**", "/css/**", "/nkey/open/**", "/test/**", "/users/join",
        "/example/city", "/email/**", "/users/help", "/users/help/**", "/users/join/**", "/users/file/**", "/app/login",
        "/login/**", "/users/profile/**", "/webSocket/**", "/tracks/info"};
    private final String[] USER_URL = {"/users/logout"};
    private final String[] ADMIN_URL = {"/cron/**"};


    @Bean
    public SecurityFilterChain webSecurityFilterChain(TokenProvider tokenProvider,
        HttpSecurity http) throws Exception {

        /**
         * cors
         * Cross-Origin Resource Sharing
         * 교차 출처의 개념 > 다른 출처
         * -> 다른 출처와 리소스를 공유하는 것
         *
         * csrf
         * cross-site Request forgey
         *
         *
         * dispatcherTypeMatchers 부분의 설정은 스프링 부트 3.0부터 적용된 스프링 시큐리티 6.0 부터
         * forward 방식 페이지 이동에도 default로 인증이 걸리도록 변경되어서 JSP나 타임리프 등
         * 컨트롤러에서 화면 파일명을 리턴해 ViewResolver가 작동해 페이지 이동을 하는 경우 위처럼 설정을 추가하셔야 합니다.
         * 요청을 위조하여 사용자의 권한을 이용해 서버에 대한 악성공격을 하는 것
         */

        /**
         * rest api를 이용한 서버라면, session 기반 인증과는 다르게 stateless하기 때문에 서버에 인증정보를 보관하지 않는다.
         * rest api에서 client는 권한이 필요한 요청을 하기 위해서는 요청에 필요한 인증 정보를(OAuth2, jwt토큰 등)을 포함시켜야 한다.
         * 따라서 서버에 인증정보를 저장하지 않기 때문에 굳이 불필요한 csrf 코드들을 작성할 필요가 없다.
         */
// 허용 파일 및 허용 url
        http.csrf().disable().exceptionHandling().
            authenticationEntryPoint(jwtAuthenticationEntryPoint).
            accessDeniedHandler(jwtAccessDeniedHandler).and().
            authorizeHttpRequests(request ->
                {
                    try {
                        request.
                            requestMatchers(ALL_URL).permitAll().
                            requestMatchers("/favicon.ico").permitAll().
                            anyRequest().authenticated()
                            .and().apply(new JwtSecurityConfig(tokenProvider));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                // 어떠한 요청이라도 인증필요
            );
        return http.build();
    }

    @Bean
    public HttpSessionSecurityContextRepository httpSessionSecurityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public SecurityContextImpl securityContext() {
        return new SecurityContextImpl();
    }

/*    @Bean
    public RememberMeServices rememberMeServices(UserDetailsService userDetailsService,
        UserLoginStatusService userLoginStatusService) {
        return new RedisRememberService(RememberCookie.KEY.getValue(), userDetailsService, redisQueryService,
            userLoginStatusService);
    }*/

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
        throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
