package com.cbw.security.jwt.global.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/* application.yml에 기입한 정보를 객체로 매핑하여 사용하기 위해 선언합니다. */

@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
	private String header;
    private String secret;
    private String refreshTokenSecret;
    private Long accessTokenValidityInSeconds;
    private Long refreshTokenValidityInSeconds;
}
