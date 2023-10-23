package com.cbw.security.jwt.account.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.cbw.security.jwt.account.dto.RequestAccount;
import com.cbw.security.jwt.account.dto.ResponseAccount;
import com.cbw.security.jwt.account.service.AccountService;
import com.cbw.security.jwt.global.dto.CommonResponse;
import com.cbw.security.jwt.global.exception.error.InvalidRefreshTokenException;
import com.cbw.security.jwt.global.security.CustomJwtFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController
{
    private final AccountService accountService;

    // 생성자주입
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/token") // Account 계정인증 API
    public ResponseEntity<CommonResponse> authorize(@Valid @RequestBody RequestAccount.Login loginDto) {

        ResponseAccount.Token token = accountService.authenticate(loginDto.username(), loginDto.password());

        // response header 에도 넣고 응답 객체에도 넣는다.
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(CustomJwtFilter.AUTHORIZATION_HEADER, "Bearer " + token.accessToken());
        httpHeaders.add(CustomJwtFilter.REFRESH_HEADER, "Bearer " + token.refreshToken());

        // 응답
        CommonResponse response = CommonResponse.builder()
                .success(true)
                .response(token)
                .build();

        return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
    }

    @PutMapping("/token") // 리프레시 토큰을 활용한 액세스 토큰 갱신
    public ResponseEntity<CommonResponse> refreshToken(@Valid @RequestBody RequestAccount.Refresh refreshDto) {

        ResponseAccount.Token token = accountService.refreshToken(refreshDto.token());

        // response header 에도 넣고 응답 객체에도 넣는다.
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(CustomJwtFilter.AUTHORIZATION_HEADER, "Bearer " + token.accessToken());

        // 응답
        CommonResponse response = CommonResponse.builder()
                .success(true)
                .response(token)
                .build();

        return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
    }
    
    
    @PutMapping("/refresh") // 리프레시 토큰(헤더값)을 활용한 액세스 토큰 갱신
    public ResponseEntity<CommonResponse> refreshHeadToken(HttpServletRequest request) {
    	
    	String bearerToken = request.getHeader(CustomJwtFilter.REFRESH_HEADER);
    	
    	System.out.println("bearerToken : " + bearerToken);
    	
  		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
  			
  			System.out.println("bearerToken.substring(7) : " + bearerToken.substring(7));
  			
  			ResponseAccount.Token token = accountService.refreshToken(bearerToken.substring(7));
  			 // response header 에도 넣고 응답 객체에도 넣는다.
  	        HttpHeaders httpHeaders = new HttpHeaders();
  	        httpHeaders.add(CustomJwtFilter.AUTHORIZATION_HEADER, "Bearer " + token.accessToken());
  	        httpHeaders.add(CustomJwtFilter.REFRESH_HEADER, "Bearer " + token.refreshToken());
  	        // 응답
  	        CommonResponse response = CommonResponse.builder()
  	                .success(true)
  	            //    .response(token)
  	                .build();
  	        return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
  		} else {	
  			throw new InvalidRefreshTokenException();
  		}
    }
     

    //리프레시토큰 만료 API
    //-> 해당 계정의 가중치를 1 올린다. 그럼 나중에 해당 리프레시 토큰으로 갱신 요청이 들어와도 받아들여지지 않음
    @DeleteMapping("/{username}/token")
    @PreAuthorize("hasAnyRole('ADMIN')") // ADMIN 권한만 호출 가능
    public ResponseEntity<CommonResponse> authorize(@PathVariable String username) {
        accountService.invalidateRefreshTokenByUsername(username);
        // 응답
        CommonResponse response = CommonResponse.builder()
                .success(true)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    // 계정 탈퇴 구현 시 계정을 삭제하지 않고 비활성화 시켜야한다. field activated
    
    
    
    
    @GetMapping("/tokenRefreshTime") // 액세스 토큰 유효시간을 확인한다.
    public void tokenRefreshTime(@Valid @RequestBody RequestAccount.Access AccessDto) {
       accountService.tokenRefreshTime(AccessDto.accessToken());
    }
    

    @PutMapping("/tokenRefreshAccessToken") // 액세스 토큰 유효시간 만료 5분전 재발급한다.
    public ResponseEntity<CommonResponse> tokenRefreshAccessToken(@Valid @RequestBody RequestAccount.RefreshAccess refreshAccessDto) {

        ResponseAccount.Token token = accountService.refreshToken(refreshAccessDto.refreshToken());

        // response header 에도 넣고 응답 객체에도 넣는다.
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(CustomJwtFilter.AUTHORIZATION_HEADER, "Bearer " + token.accessToken());

        // 응답
        CommonResponse response = CommonResponse.builder()
                .success(true)
                .response(token)
                .build();

        return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
    }
    
}