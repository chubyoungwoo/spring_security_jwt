package com.cbw.security.jwt.global.security.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cbw.security.jwt.global.security.RefreshTokenProvider;
import com.cbw.security.jwt.global.security.TokenProvider;

/* JwtConfig는 JWT 설정파일로 TokenProvider에 의존성을 주입하고 빈을 생성하는 역할을 수행합니다. */

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {
	
	// 액세스 토큰 발급용, 리프레시 토큰 발급용은 각각 별도의 키와 유효기간을 갖는다.
	@Bean(name = "tokenProvider")
	public TokenProvider tokenProvider(JwtProperties jwtProperties) {
		return new TokenProvider(jwtProperties.getSecret(), jwtProperties.getAccessTokenValidityInSeconds());
	}
	
	// 리프레시 토큰은 별도의 키를 가지기 때문에 리프레시 토큰으로는 API 호출 불가
	// 액세스 토큰 재발급 시 검증용
	@Bean(name = "refreshTokenProvider")
	public RefreshTokenProvider refreshTokenProvider(JwtProperties jwtProperties) {
		return new RefreshTokenProvider(jwtProperties.getRefreshTokenSecret(),jwtProperties.getRefreshTokenValidityInSeconds());
	}
	
}
