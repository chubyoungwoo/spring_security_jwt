package com.cbw.security.jwt.global.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 실질적으로 액세스토큰을 검증하는 역할을 수행하는 GenericFilterBean을 상속받아 CustomJwtFilter를 작성합니다.
 * 우리가 눈여겨보아야할 곳은 doFilter 메서드 영역입니다.
 * 해당 코드는 필터 통과 시 토큰의 유효성을 검증하고, 토큰에서 식별자인 username과 해당 토큰에 부여된 권한을 뽑아 스프링 시큐리티 Authentication 객체를 생성하고 시큐리티 컨텍스트에 저장합니다.
 * 이 말은 토큰 검증을 하며 데이터베이스에 사용자가 존재하는지 조회하지 않는다는 것입니다. doFilter을 통과시켜 시큐리티 컨텍스트에 저장된 authentication는 유저이름과 권한정보만 담고 있으므로
 * 해당 사용자가 실제로 존재하는 지, 혹은 해당 사용자에 대한 다른 정보를 얻고싶다면 별도로 조회해야합니다.
 */

@Component
public class CustomJwtFilter extends GenericFilterBean {
	private static final Logger logger = LoggerFactory.getLogger(CustomJwtFilter.class);
	
	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String REFRESH_HEADER = "Refresh";
	
	private TokenProvider tokenProvider;
	
	public CustomJwtFilter(TokenProvider tokenProvider) {
		this.tokenProvider = tokenProvider;
	}

	// 실제 필터링 로직은 doFilter 안에 들어가게 된다. GenericFilterBean을 받아 구현
    // Dofilter는 토큰의 인증정보를 SecurityContext 안에 저장하는 역할 수행
    // 현재는 jwtFilter 통과 시 loadUserByUsername을 호출하여 디비를 거치지 않으므로 시큐리티 컨텍스트에는 엔티티 정보를 온전히 가지지 않는다
    // 즉 loadUserByUsername을 호출하는 인증 API를 제외하고는 유저네임, 권한만 가지고 있으므로 Account 정보가 필요하다면 디비에서 꺼내와야함
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		
		 HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
	     String jwt = resolveToken(httpServletRequest);
	     String requestURI = httpServletRequest.getRequestURI();
		// 유효성 검증
	     if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
	    	// 토큰에서 유저네임, 권한을 뽑아 스프링 시큐리티 유저를 만들어 Authentication(인증) 반환
	 		Authentication authentication = tokenProvider.getAuthentication(jwt);
	 		// 해당 스프링 시큐리티 유저를 시큐리티 컨텍스트에 저장, 디비를 거치지 않음
	 		SecurityContextHolder.getContext().setAuthentication(authentication);
	 		logger.debug("Security Context에 '{}' 인증 정보를 저장했습니다. uri: {}", authentication.getName(), requestURI);
	     } else {
	    	 logger.debug("유효한 JWT 토큰이 없습니다. uri: {}", requestURI);
	     }
	     
	    filterChain.doFilter(servletRequest, servletResponse);	
		
	}
	
	//헤더에서 토큰 정보를 꺼내온다.
	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

}
