package sky.board.config;


import static org.springframework.security.config.Customizer.withDefaults;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import sky.board.domain.user.service.UserLogService;
import sky.board.domain.user.utill.Filter.CustomUsernameFilter;
import sky.board.domain.user.utill.handler.CustomAuthenticationFailHandler;
import sky.board.domain.user.utill.handler.CustomAuthenticationSuccessHandler;
import sky.board.global.openapi.service.ApiExamCaptchaNkeyService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {


    /**
     * 에외 처리하고 싶은 url
     *
     * @return
     *//*
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/login/dashboard", "/email/**", "/join/**", "/");
    }*/
    @Bean
    public SecurityFilterChain webSecurityFilterChain(AuthenticationManager authenticationManager,
        UserLogService userLogService,
        CustomAuthenticationSuccessHandler successHandler,
        CustomAuthenticationFailHandler failHandler,
        ApiExamCaptchaNkeyService apiExamCaptchaNkeyService,
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
         * .rememberMeParameter() 사용자가 로그인 페이지를 만들때 여기를 주의해야 한다.
         * 보통 checkbox로 만들텐데, 이때 name을 여기서 설정한 값과 동일하게 해줘야 한다.
         * tokenValiditySeconds 초 단위로 remember-me 토큰의 유효시간을 설정할 수 있다.
         * .alwaysRemember 사용자가 이 기능을 사용한다고 체크하지 않아도 자동으로 사용하게 하는 기능이다.
         * userDetailsService 이 기능을 사용하려면 사용자 정보가 필요하다. 따라서 객체를 주입받은 후 넣어주자.
         */
  /*      http.rememberMe()// rememberMe 기능 작동함
            .rememberMeParameter("rememberMe") // default: remember-me, checkbox 등의 이름과 맞춰야함
            .alwaysRemember(false)  // 사용자가 체크박스를 활성화하지 않아도 항상 실행, default: false
            .userDetailsService(userDetailsService);*/
        // 기능을 사용할 때 사용자 정보가 필요함. 반드시 이 설정 필요함.
        //tokenValiditySeconds(3600) // 쿠키의 만료시간 설정(초), default: 14일
        http.csrf().disable().
            authorizeHttpRequests(request ->
                request.
                    dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll().
                    requestMatchers(
                        "/js/common/**",
                        "/js/error/**",
                        "/js/join/**",
                        "/login/**",
                        "/email/**",
                        "/join/**",
                        "/example/city",
                        "/login",
                        "/login/logout",
                        "/test/**",
                        "/",
                        "/test",
                        "/css/**"). // 허용 파일 및 허용 url
                    permitAll().
                    anyRequest().
                    authenticated() // 어떠한 요청이라도 인증필요
            ).
            formLogin(login -> login. //form 방식 로그인 사용
                    loginPage("/login"). // 커스텀 로그인 페이지 지정
                    usernameParameter("userId"). // submit 유저아이디 input 에 아이디,네임 속성명
                    passwordParameter("password"). // submit 패스워드 input 에 아이디,네임 속성명
//                    successHandler(successHandler).
//                    failureHandler(failHandler).
    permitAll()
                // 대시보드 이동이 막히면 안되므로 얘는 허용
//                defaultSuccessUrl("/login/dashboard").
            ).logout(withDefaults()).
            addFilterBefore(new CustomUsernameFilter(authenticationManager, userLogService, successHandler, failHandler,
                    apiExamCaptchaNkeyService),
                UsernamePasswordAuthenticationFilter.class); // 로그아웃은 기본설정으로 (/logout으로 인증해제)
        /**
         *     loginProcessingUrl("/login")
         * 지정하지 않을 시 기본은 'POST /login'
         *
         */
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
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
        throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
