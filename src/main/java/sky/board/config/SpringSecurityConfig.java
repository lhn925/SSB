package sky.board.config;


import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;
import org.springframework.util.StringUtils;
import sky.board.domain.user.model.RememberCookie;
import sky.board.domain.user.model.UserGrade;
import sky.board.domain.user.service.login.RedisRememberService;
import sky.board.domain.user.service.log.UserLoginLogService;
import sky.board.domain.user.service.login.UserLoginStatusService;
import sky.board.domain.user.utili.CustomCookie;
import sky.board.domain.user.utili.Filter.ApiKeyAuthFilter;
import sky.board.domain.user.utili.Filter.ApikeyAuthExceptionHandlerFilter;
import sky.board.domain.user.utili.Filter.CustomLogoutFilter;
import sky.board.domain.user.utili.Filter.CustomRememberMeAuthenticationFilter;
import sky.board.domain.user.utili.Filter.CustomUsernameFilter;
import sky.board.domain.user.utili.handler.login.CustomAuthenticationFailHandler;
import sky.board.domain.user.utili.handler.login.CustomAuthenticationSuccessHandler;
import sky.board.domain.user.utili.handler.login.CustomCookieLoginSuccessHandler;
import sky.board.domain.user.utili.handler.logout.CustomSimpleUrlLogoutSuccessHandler;
import sky.board.global.openapi.service.ApiExamCaptchaNkeyService;
import sky.board.global.redis.service.RedisService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SpringSecurityConfig {


    private final RedisService redisService;
    private final UserLoginLogService userLoginLogService;
    private final ApiExamCaptchaNkeyService apiExamCaptchaNkeyService;


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
        "/example/city", "/email/**", "/user/help/**", "/user/join/**", "/login", "/login/**"};
    private final String[] USER_URL = {"/user/myInfo/**"};
    private final String[] ADMIN_URL = {"/board"};


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
        CustomCookieLoginSuccessHandler cookieLoginSuccessHandler,
        CustomAuthenticationFailHandler failHandler,
        CustomAuthenticationSuccessHandler successHandler,
        CustomSimpleUrlLogoutSuccessHandler logoutSuccessHandler,
        UserLoginStatusService userLoginStatusService,
        AuthenticationManager authenticationManager,
        UserDetailsService userDetailsService,
        RememberMeServices rememberMeServices,
        MessageSource messageSource,
        HttpSecurity http) throws Exception {
        ApiKeyAuthFilter apiKeyAuthFilter = new ApiKeyAuthFilter("/user/myInfo/api/**", messageSource);
        ApikeyAuthExceptionHandlerFilter apikeyAuthExceptionHandlerFilter = new ApikeyAuthExceptionHandlerFilter(
            messageSource);
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
                successHandler, failHandler,
                apiExamCaptchaNkeyService),
            UsernamePasswordAuthenticationFilter.class);
        http.addFilterAt(
            new CustomLogoutFilter(logoutSuccessHandler, userLoginStatusService), LogoutFilter.class);
        http.addFilterBefore(
            new CustomRememberMeAuthenticationFilter(authenticationManager, rememberMeServices,
                cookieLoginSuccessHandler),
            RememberMeAuthenticationFilter.class);

        http.addFilterBefore(apiKeyAuthFilter, BasicAuthenticationFilter.class);

// 허용 파일 및 허용 url
        http.csrf().disable().cors().disable().
            authorizeHttpRequests(request ->
                    request.
                        dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll().
                        requestMatchers(ALL_URL).permitAll().
                        requestMatchers(USER_URL).hasRole(UserGrade.USER.getDescription()).
                        anyRequest()
                        .authenticated()
                // 어떠한 요청이라도 인증필요
            ).formLogin(login -> login. //form 방식 로그인 사용
                loginPage("/login"). // 커스텀 로그인 페이지 지정
                usernameParameter("userId"). // submit 유저아이디 input 에 아이디,네임 속성명
                passwordParameter("password") // submit 패스워드 input 에 아이디,네임 속성명
                .permitAll()
            );

        // logout 구현 부분
        http.logout()
            .logoutUrl("/logout")
            .logoutSuccessHandler(logoutSuccessHandler) // 로그아웃 성공 핸들러
            .deleteCookies(RememberCookie.KEY.getValue());// 로그아웃 후 삭제할 쿠키 지정

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
    public RememberMeServices rememberMeServices(UserDetailsService userDetailsService,UserLoginStatusService userLoginStatusService) {
        return new RedisRememberService(RememberCookie.KEY.getValue(), userDetailsService, redisService, userLoginStatusService);
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
