package com.cbw.security.jwt.global.security.handler;

import com.cbw.security.jwt.global.dto.CommonResponse;
import com.cbw.security.jwt.global.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
 * AccessDeniedHandler는 권한 체크 후 인가 실패 시 동작하도록 시큐리티 설정파일에 설정할 예정입니다.
 * AccessDeniedHandler를 상속받아 구현합니다.
*/

//인가 실패
@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
 private final ObjectMapper objectMapper;

 @Override
 public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
     //필요한 권한이 없이 접근하려 할때 403
     ErrorResponse error = ErrorResponse.builder()
             .status(HttpStatus.FORBIDDEN.value())
             .message("FORBIDDEN")
             .code("AUTH")
             .build();

     response.setContentType("application/json");
     response.setStatus(HttpServletResponse.SC_FORBIDDEN);
     response.getOutputStream().println(objectMapper.writeValueAsString(CommonResponse.builder()
             .success(false)
             .error(error)
             .build()));
 }
}