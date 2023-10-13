package com.cbw.security.jwt.account.dto;

import lombok.Builder;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RequestAccount() {
    @Builder
    public record Login(
            @NotNull
            @Size(min = 3, max = 50)
            String username,
            @NotNull
            @Size(min = 5, max = 100)
            String password
    ) {
    }

    @Builder
    public record Refresh(
            @NotNull
            String token
    ) {
    }
    
    @Builder
    public record Access(
            @NotNull
            String accessToken
    ) {
    }
    
    @Builder
    public record RefreshAccess(
    		@NotNull
            String refreshToken,
            @NotNull
            String accessToken
    ) {
    }
}
