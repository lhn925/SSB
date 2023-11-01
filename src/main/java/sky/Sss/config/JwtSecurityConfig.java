package sky.Sss.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sky.Sss.domain.user.utili.jwt.JwtAuthenticationEntryPoint;
import sky.Sss.domain.user.utili.jwt.JwtFilter;
import sky.Sss.domain.user.utili.jwt.TokenProvider;

@RequiredArgsConstructor
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private final TokenProvider tokenProvider;

    @Override
    public void configure(HttpSecurity http) {
        // security 로직에 JwtFilter 등록
        http.addFilterBefore(
            new JwtFilter(tokenProvider),
            UsernamePasswordAuthenticationFilter.class
        );
    }
}