package com.cbw.security.jwt.global.security;

import java.security.InvalidParameterException;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

// 토큰 생성,검증
public class TokenProvider {

	protected final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
	
	protected static final String AUTHORITIES_KEY = "auth";
	
	protected final String secret;
	protected final long tokenValidityInMillisseconds;
	protected final long tokenValidityInSeconds;
	
	
	protected Key key;
	
	public TokenProvider (String secret, long tokenValidityInSeconds) {
		this.secret = secret;
		this.tokenValidityInSeconds = tokenValidityInSeconds;
		this.tokenValidityInMillisseconds = tokenValidityInSeconds * 1000;	
		
		//시크릿 값을 decode해서 키 변수에 할당
		byte[] keyBytes = Decoders.BASE64.decode(secret);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}
	
	// 토큰 생성
	public String createToken(Authentication authentication) {
		String authorities = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(","));
		long now = (new Date()).getTime();
		Date validity = new Date(now + this.tokenValidityInMillisseconds);
		
		return Jwts.builder()
				.setSubject(authentication.getName())
				.claim(AUTHORITIES_KEY,authorities)
				.signWith(key, SignatureAlgorithm.HS512)
				.setExpiration(validity)
				.compact();
	}
	
	// 토큰을 받아 클레임을 만들고 권한정보를 빼서 시큐리티 유저객체를 만들어 Authentication(인증) 객체 반환
	public Authentication getAuthentication(String token) {
		Claims claims = Jwts
				.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody();
		
		Collection<? extends GrantedAuthority> authorities =
				Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
				      .map(SimpleGrantedAuthority::new)
				      .collect(Collectors.toList());
		// 디비를 거치지 않고 토큰에서 값을 꺼내 바로 시큐리티 유저 객체를 만들어 Authentication을 만들어 반환하기에 유저네임, 권한 외 정보는 알 수 없다.
		User principal = new User(claims.getSubject(), "", authorities);
		return new UsernamePasswordAuthenticationToken(principal, token,authorities);
		
	}
	
	// 토큰 유효성 검사
	public boolean validateToken(String token) {
		try {
			Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
			return true;
		} catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
			logger.info("잘못된 JWT 서명입니다.");
		} catch (ExpiredJwtException e) {
			logger.info("만료된 JWT 토클입니다.");
		} catch (UnsupportedJwtException e) {
			logger.info("지원되지 않는 JWT 토큰입니다.");
		} catch (IllegalArgumentException e) {
			logger.info("JWT 토큰이 잘못되었습니다.");
		}
		return false;
	}
	
	//Token 재발급 여부 유효시간 5분전 재발급 일림(Second)
	public boolean isTokenExpired(String token) {
		Date expiration = validTokenAndReturnBody(token).getExpiration();
		Date currentDate = new Date();
		
		long Sec = (expiration.getTime() - currentDate.getTime()  ) / 1000; // 초
		
		System.out.println(expiration + " 토큰만료일자");
		System.out.println(currentDate + " 현재일자");
		System.out.println(Sec + "초차이");
		
	
		System.out.println(this.tokenValidityInSeconds + " 300초(5분)전 토큰 재발급");
		
		if( Sec < 300) {  //토큰 재발급
			return true;
		}
		return false;
	}
	
	 // 토큰 해석
	  public Claims validTokenAndReturnBody(String token) {
	    try {
	      return Jwts.parserBuilder()
	          .setSigningKey(key)
	          .build()
	          .parseClaimsJws(token)
	          .getBody();
	    } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
			logger.info("잘못된 JWT 서명입니다.");
			throw new InvalidParameterException("잘못된 JWT 서명입니다.");
		} catch (ExpiredJwtException e) {
			logger.info("만료된 JWT 토클입니다.");
			throw new InvalidParameterException("만료된 JWT 토클입니다.");
		} catch (UnsupportedJwtException e) {
			logger.info("지원되지 않는 JWT 토큰입니다.");
			throw new InvalidParameterException("지원되지 않는 JWT 토큰입니다.");
		} catch (IllegalArgumentException e) {
			logger.info("JWT 토큰이 잘못되었습니다.");
			throw new InvalidParameterException("JWT 토큰이 잘못되었습니다.");
		}
	    
	  }
}
