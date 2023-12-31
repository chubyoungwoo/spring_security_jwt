package com.cbw.security.jwt.account.service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cbw.security.jwt.account.domain.Account;
import com.cbw.security.jwt.account.domain.AccountAdapter;
import com.cbw.security.jwt.account.dto.ResponseAccount;
import com.cbw.security.jwt.account.repository.AccountRepository;
import com.cbw.security.jwt.global.exception.error.InvalidRefreshTokenException;
import com.cbw.security.jwt.global.security.RefreshTokenProvider;
import com.cbw.security.jwt.global.security.TokenProvider;

@Service
public class AccountServiceImpl implements AccountService
{
    private final TokenProvider tokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final AccountRepository accountRepository;

    public AccountServiceImpl(TokenProvider tokenProvider, RefreshTokenProvider refreshTokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder, AccountRepository accountRepository) {
        this.tokenProvider = tokenProvider;
        this.refreshTokenProvider = refreshTokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.accountRepository = accountRepository;
    }

    // username 과 패스워드로 사용자를 인증하여 액세스토큰과 리프레시 토큰을 반환한다.
    @Transactional
    @Override
    public ResponseAccount.Token authenticate(String username, String password) {
    	
    	System.out.println("authenticate 1 : " + username);
    	
        // 받아온 유저네임과 패스워드를 이용해 UsernamePasswordAuthenticationToken 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);
        
        System.out.println("authenticate 2 : " + username);

        // authenticationToken 객체를 통해 Authentication 객체 생성
        // 이 과정에서 UserDetailServiceImpl 에서 우리가 재정의한 loadUserByUsername 메서드 호출
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        
        System.out.println("authenticate 3 : " + username);

        // 인증 정보를 기준으로 jwt access 토큰 생성
        String accessToken = tokenProvider.createToken(authentication);

        System.out.println("authenticate 4 : " + username);
        
        // 위에서 loadUserByUsername를 호출하였으므로 AccountAdapter가 시큐리티 컨텍스트에 저장되어 Account 엔티티 정보를 우리는 알 수 있음
        // 유저 정보에서 가중치를 꺼내 리프레시 토큰 가중치에 할당, 나중에 액세스토큰 재발급 시도 시 유저정보 가중치 > 리프레시 토큰이라면 실패
        Long tokenWeight = ((AccountAdapter)authentication.getPrincipal()).getAccount().getTokenWeight();
        
        System.out.println("tokenWeight : " + tokenWeight);
        
        String refreshToken = refreshTokenProvider.createToken(authentication, tokenWeight);

        /* 로그인이 된경우 refreshToken 과 가중치를 초기화 한다. */
        Account account = accountRepository.findOneWithAuthoritiesByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + "-> 찾을 수 없습니다"));

        account.updateRefreshToken(refreshToken,1L); // 더티체킹에 의해 엔티티 변화 반영
   
        return ResponseAccount.Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    @Override
    public ResponseAccount.Token refreshToken(String refreshToken) {
        // 먼저 리프레시 토큰을 검증한다.
        if(!refreshTokenProvider.validateToken(refreshToken)) throw new InvalidRefreshTokenException();

        // 리프레시 토큰 값을 이용해 사용자를 꺼낸다.
        // refreshTokenProvider과 TokenProvider는 다른 서명키를 가지고 있기에 refreshTokenProvider를 써야함
        Authentication authentication = refreshTokenProvider.getAuthentication(refreshToken);
        Account account = accountRepository.findOneWithAuthoritiesByUsername(authentication.getName())
                .orElseThrow(()-> new UsernameNotFoundException(authentication.getName() + "을 찾을 수 없습니다"));
        
        System.out.println("refreshToken : " + refreshToken);
        System.out.println("DB RefreshToken : " + account.getRefreshToken());
        // 리프레쉬 토큰이 없으면 에러 처리
        if(account.getRefreshToken() == null ) {
        	throw new UsernameNotFoundException("리프레쉬 토큰을 찾을 수 없습니다");
        }
        // 사용자 디비 값에 있는 것과 가중치 비교, 디비 가중치가 더 크거나 db에 저장된 리프레쉬 토큰과 같이 다르면 유효하지 않음
        if(account.getTokenWeight() > refreshTokenProvider.getTokenWeight(refreshToken) || !account.getRefreshToken().equals(refreshToken) ) throw new InvalidRefreshTokenException();

        // 리프레시 토큰에 담긴 값을 그대로 액세스 토큰 생성에 활용한다.
        String accessToken = tokenProvider.createToken(authentication);
        
        
        // 리프레시 토큰 유효시간이 1시간전이면 리프레시 토큰을 재발행한다.
        //if(tokenProvider.isTokenExpired(refreshToken)) {  액세스토큰 유효시간
        if(refreshTokenProvider.isTokenExpired(refreshToken)) {
			System.out.println("리프레시 토큰재발급");
			Long tokenWeight = account.getTokenWeight();
	        
	        System.out.println("리프레시토큰 재발급 tokenWeight : " + tokenWeight);
	        
	        refreshToken = refreshTokenProvider.createToken(authentication, tokenWeight);
	        
	        System.out.println("재발급된 리프레시토큰 : " + refreshToken);

	        account.updateRefreshToken(refreshToken,1L); // 더티체킹에 의해 엔티티 변화 반영
		}
                
        // 기존 리프레시 토큰과 새로 만든 액세스 토큰을 반환한다.
        return ResponseAccount.Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // Account 가중치를 1 올림으로써 해당 username 리프레시토큰 무효화
    @Transactional
    @Override
    public void invalidateRefreshTokenByUsername(String username) {
        Account account = accountRepository.findOneWithAuthoritiesByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + "-> 찾을 수 없습니다"));
        account.increaseTokenWeight(); // 더티체킹에 의해 엔티티 변화 반영
    }

	@Override
	public void tokenRefreshTime(String accessToken) {
		if(tokenProvider.isTokenExpired(accessToken)) {
			System.out.println("토큰재발급");
		}
		
	}
}
