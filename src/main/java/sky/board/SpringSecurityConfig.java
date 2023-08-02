package sky.board;


import static org.springframework.security.config.Customizer.withDefaults;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sky.board.domain.user.utill.Filter.CustomUsernamePasswordAuthenticationFilter;
import sky.board.global.handler.CustomAuthenticationFailHandler;
import sky.board.global.handler.CustomAuthenticationSuccessHandler;

//@EnableWebSecurity
//@EnableMethodSecurity
@Configuration
public class SpringSecurityConfig {


    /*   */

    /**
     * 에외 처리하고 싶은 url
     *
     * @return
     */
/*    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/login/dashboard", "/email/**", "/join/**", "/");
    }*/
    @Bean
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
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
//        http.addFilter(usernamePasswordAuthenticationFilter(http));
        // Filter 추가

        http.csrf().disable().cors().disable().
            addFilterBefore(customAuthenticationFilter(http), UsernamePasswordAuthenticationFilter.class).
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
                        "/",
                        "/login",
                        "/css/**"). // 허용 파일 및 허용 url
                    permitAll().
                    anyRequest().authenticated() // 어떠한 요청이라도 인증필요
            ).
            formLogin(login -> login. //form 방식 로그인 사용
                    loginPage("/login"). // 커스텀 로그인 페이지 지정
                    loginProcessingUrl("/login"). // submit 받을 Url post
                    usernameParameter("userId"). // submit 유저아이디 input 에 아이디,네임 속성명
                    passwordParameter("password"). // submit 패스워드 input 에 아이디,네임 속성명
                    permitAll()
                // 대시보드 이동이 막히면 안되므로 얘는 허용
//                defaultSuccessUrl("/login/dashboard").
            ).logout(withDefaults()); // 로그아웃은 기본설정으로 (/logout으로 인증해제)

        /**
         *     loginProcessingUrl("/login")
         * 지정하지 않을 시 기본은 'POST /login'
         *
         */
        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
        throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CustomUsernamePasswordAuthenticationFilter customAuthenticationFilter(HttpSecurity httpSecurity)
        throws Exception {
        return new CustomUsernamePasswordAuthenticationFilter(
            authenticationManager(httpSecurity.getSharedObject(AuthenticationConfiguration.class)));
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
