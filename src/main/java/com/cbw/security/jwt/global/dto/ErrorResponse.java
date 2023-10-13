package com.cbw.security.jwt.global.dto;

import lombok.Builder;

@Builder
public record ErrorResponse(
        String code,
        String message,
        int status
) {

}
