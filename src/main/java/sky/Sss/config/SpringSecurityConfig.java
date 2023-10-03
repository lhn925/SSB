package sky.Sss.config;


import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import sky.Sss.domain.user.model.RememberCookie;
import sky.Sss.domain.user.model.UserGrade;
import sky.Sss.domain.user.service.login.RedisRememberService;
import sky.Sss.domain.user.service.log.UserLoginLogService;
import sky.Sss.domain.user.service.login.UserLoginStatusService;
import sky.Sss.domain.user.utili.Filter.ApiKeyAuthFilter;
import sky.Sss.domain.user.utili.Filter.CustomRememberMeAuthenticationFilter;
import sky.Sss.domain.user.utili.Filter.CustomUsernameFilter;
import sky.Sss.domain.user.utili.handler.login.CustomAuthenticationFailHandler;
import sky.Sss.domain.user.utili.handler.login.CustomAuthenticationSuccessHandler;
import sky.Sss.domain.user.utili.handler.login.CustomCookieLoginSuccessHandler;
import sky.Sss.domain.user.utili.handler.logout.CustomSimpleUrlLogoutSuccessHandler;
import sky.Sss.global.openapi.service.ApiExamCaptchaNkeyService;
import sky.Sss.global.redis.service.RedisService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SpringSecurityConfig  {


    private final RedisService redisService;
    private final UserLoginLogService userLoginLogService;
    private final ApiExamCaptchaNkeyService apiExamCaptchaNkeyService;
    private final MessageSource messageSource;
    private final UserDetailsService userDetailsService;
    private final CustomAuthenticationFailHandler failHandler;


/*       "/js/**",
           "/login/**",
           "/email/**",
           "/image/**",
           "/example/city",
           "/login",
           "/user/join/api/**",
           "/user/join/**",
           "/user/help/**",
           "/test/**",
           "/open/**",
           "/",
           "/css/**"*/


    private final String[] ALL_URL = {"/", "/js/**", "/css/**", "/Nkey/open/**", "/test/**",
        "/example/city", "/email/**", "/user/help/**", "/user/join/**", "/login", "/login/**","/user/file/**"};
    private final String[] USER_URL = {"/user/logout", "/user/myInfo/**"};
    private final String[] ADMIN_URL = {"/cron/**"};




    /**
     * ㅡㅛ
     * 에외 처리하고 싶은 url
     *
     * @return
     *//*
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/login/dashboard", "/email/**", "/join/**", "/");
    }*/
    @Bean
    public SecurityFilterChain webSecurityFilterChain(
        AuthenticationManager authenticationManager,
        RememberMeServices rememberMeServices,
        CustomCookieLoginSuccessHandler customCookieLoginSuccessHandler,
        CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler,
        CustomSimpleUrlLogoutSuccessHandler logoutSuccessHandler,
        HttpSecurity http) throws Exception {
        ApiKeyAuthFilter apiKeyAuthFilter = new ApiKeyAuthFilter("/user/myInfo/api/**", messageSource);

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
        http.rememberMe()// rememberMe 기능 작동함
            .rememberMeParameter(RememberCookie.KEY.getValue()) // default: remember-me, checkbox 등의 이름과 맞춰야함
            .alwaysRemember(false)  // 사용자가 체크박스를 활성화하지 않아도 항상 실행, default: false
            .rememberMeCookieName(RememberCookie.KEY.getValue())
            .userDetailsService(userDetailsService);
        // 기능을 사용할 때 사용자 정보가 필요함. 반드시 이 설정 필요함.
        //tokenValiditySeconds(3600) // 쿠키의 만료시간 설정(초), default: 14일

        http.addFilterBefore(new CustomUsernameFilter(
                rememberMeServices, authenticationManager,
                userLoginLogService,
                customAuthenticationSuccessHandler, failHandler,
                apiExamCaptchaNkeyService),
            UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(
            new CustomRememberMeAuthenticationFilter(authenticationManager, rememberMeServices,
                customCookieLoginSuccessHandler),
            RememberMeAuthenticationFilter.class);

        http.addFilterBefore(apiKeyAuthFilter, BasicAuthenticationFilter.class);

        /**
         * rest api를 이용한 서버라면, session 기반 인증과는 다르게 stateless하기 때문에 서버에 인증정보를 보관하지 않는다.
         * rest api에서 client는 권한이 필요한 요청을 하기 위해서는 요청에 필요한 인증 정보를(OAuth2, jwt토큰 등)을 포함시켜야 한다.
         * 따라서 서버에 인증정보를 저장하지 않기 때문에 굳이 불필요한 csrf 코드들을 작성할 필요가 없다.
         */
// 허용 파일 및 허용 url
        http.csrf().disable().
            authorizeHttpRequests(request ->
                    request.
                        dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll().
                        requestMatchers(ALL_URL).permitAll().
                        requestMatchers(USER_URL).hasRole(UserGrade.USER.getDescription()).
                        requestMatchers(ADMIN_URL).hasRole(UserGrade.ADMIN.getDescription()).
                        anyRequest()
                        .authenticated()
                // 어떠한 요청이라도 인증필요
            ).formLogin(login -> login. //form 방식 로그인 사용
                loginPage("/login"). // 커스텀 로그인 페이지 지정
                usernameParameter("userId"). // submit 유저아이디 input 에 아이디,네임 속성명
                passwordParameter("password") // submit 패스워드 input 에 아이디,네임 속성명
                .permitAll()
            );
//        http.csrf().ignoringRequestMatchers(ALL_URL);
        // logout 구현 부분
        http.logout()
            .logoutUrl("/logout")
            .logoutRequestMatcher(new AntPathRequestMatcher("/logout")).invalidateHttpSession(false)
            .logoutSuccessHandler(logoutSuccessHandler) // 로그아웃 성공 핸들러
            .deleteCookies(RememberCookie.KEY.getValue());

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

    @Bean
    public RememberMeServices rememberMeServices(UserDetailsService userDetailsService,
        UserLoginStatusService userLoginStatusService) {
        return new RedisRememberService(RememberCookie.KEY.getValue(), userDetailsService, redisService,
            userLoginStatusService);
    }

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
