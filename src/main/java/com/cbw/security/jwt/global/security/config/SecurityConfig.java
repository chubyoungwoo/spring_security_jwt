package com.cbw.security.jwt.global.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.CorsFilter;

import com.cbw.security.jwt.global.security.CustomJwtFilter;
import com.cbw.security.jwt.global.security.handler.JwtAccessDeniedHandler;
import com.cbw.security.jwt.global.security.handler.JwtAuthenticationEntryPoint;


@Configuration
@EnableWebSecurity     // 기본적인 웹보안을 활성화하겠다
@EnableMethodSecurity // @PreAuthorize 어노테이션 사용을 위해 선언
public class SecurityConfig {
	
	private final CorsFilter corsFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    // 생성자 통해 스프링 빈 주입받는다.
    public SecurityConfig(
            CorsFilter corsFilter,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler
    ) {
        this.corsFilter = corsFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }
    
    // BCryptPasswordEncoder 라는 패스워드 인코더 사용
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @SuppressWarnings("removal")
	@Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, CustomJwtFilter customJwtFilter) throws Exception {
        httpSecurity

	        .csrf(AbstractHttpConfigurer::disable)
	        .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)        
	        
	        .headers((headers) -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
	
	        .exceptionHandling(e -> e
	        		.authenticationEntryPoint(jwtAuthenticationEntryPoint) // 우리가 만든 클래스로 인증 실패 핸들링
	        		.accessDeniedHandler(jwtAccessDeniedHandler) // 커스텀 인가 실패 핸들링
	        )
	        		
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
	
	        .authorizeHttpRequests(auth -> auth
	                .requestMatchers(
	                        new AntPathRequestMatcher("/"),
	                        new AntPathRequestMatcher("/css/**"),
	                        new AntPathRequestMatcher("/images/**"),
	                        new AntPathRequestMatcher("/js/**"),
	                        new AntPathRequestMatcher("/h2/**"),
	                        new AntPathRequestMatcher("/favicon.ico"),
	                        new AntPathRequestMatcher("/error"),
	                        new AntPathRequestMatcher("/docs/**"),
	                        new AntPathRequestMatcher("/api/hello"),
	                        new AntPathRequestMatcher("/api/v1/accounts/token"),
	                        new AntPathRequestMatcher("/api/v1/members"),
	                        new AntPathRequestMatcher("/api/v1/accounts/tokenRefreshAccessToken"),
	                        new AntPathRequestMatcher("/profile")
	                ).permitAll()
	                .anyRequest().authenticated())
	
	        .addFilterBefore(customJwtFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
    
}
