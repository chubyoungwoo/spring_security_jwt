package com.cbw.security.jwt.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.cbw.security.jwt.global.dto.CommonResponse;
import com.cbw.security.jwt.global.dto.ErrorResponse;
import com.cbw.security.jwt.global.exception.error.DuplicateMemberException;
import com.cbw.security.jwt.global.exception.error.InvalidDataException;
import com.cbw.security.jwt.global.exception.error.InvalidRefreshTokenException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Bean Validation에 실패했을 때, 에러메시지를 내보내기 위한 Exception Handler
     */
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<CommonResponse> handleParamViolationException(BindException ex) {
        // 파라미터 validation에 걸렸을 경우
        ErrorCode errorCode = ErrorCode.REQUEST_PARAMETER_BIND_FAILED;

        ErrorResponse error = ErrorResponse.builder()
                .status(errorCode.getStatus().value())
                .message(errorCode.getMessage())
                .code(errorCode.getCode())
                .build();

        CommonResponse response = CommonResponse.builder()
                .success(false)
                .error(error)
                .build();
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    @ExceptionHandler(DuplicateMemberException.class)
    protected ResponseEntity<CommonResponse> handleDuplicateMemberException(DuplicateMemberException ex) {
        ErrorCode errorCode = ErrorCode.DUPLICATE_MEMBER_EXCEPTION;
        
        log.debug("DuplicateMemberException :" + ex);

        ErrorResponse error = ErrorResponse.builder()
                .status(errorCode.getStatus().value())
                .message(errorCode.getMessage())
                .code(errorCode.getCode())
                .build();

        CommonResponse response = CommonResponse.builder()
                .success(false)
                .error(error)
                .build();
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    protected ResponseEntity<CommonResponse> handleInvalidRefreshTokenException(InvalidRefreshTokenException ex) {
        ErrorCode errorCode = ErrorCode.INVALID_REFRESH_TOKEN;

        ErrorResponse error = ErrorResponse.builder()
                .status(errorCode.getStatus().value())
                .message(errorCode.getMessage())
                .code(errorCode.getCode())
                .build();

        CommonResponse response = CommonResponse.builder()
                .success(false)
                .error(error)
                .build();
        return new ResponseEntity<>(response, errorCode.getStatus());
    }
    
    /**
     * 데이터베이스에 일치하는 정보가 없을때, 에러메시지를 내보내기 위한 Exception Handler
     */
    @ExceptionHandler(InvalidDataException.class)
    protected ResponseEntity<CommonResponse> handleInvalidDataException(InvalidDataException ex) {

        ErrorCode errorCode = ErrorCode.INVALID_DATA_FAILED;
        
        log.debug("InvalidDataException :" + ex);

        ErrorResponse error = ErrorResponse.builder()
                .status(errorCode.getStatus().value())
                .message(errorCode.getMessage())
                .code(errorCode.getCode())
                .build();

        CommonResponse response = CommonResponse.builder()
                .success(false)
                .error(error)
                .build();
        return new ResponseEntity<>(response, errorCode.getStatus());
    }
}
