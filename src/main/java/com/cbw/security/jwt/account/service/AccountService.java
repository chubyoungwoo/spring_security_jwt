package com.cbw.security.jwt.account.service;

import com.cbw.security.jwt.account.dto.ResponseAccount;

public interface AccountService
{
    ResponseAccount.Token authenticate(String username, String password);
    ResponseAccount.Token refreshToken(String refreshToken);
    void invalidateRefreshTokenByUsername(String username);
	void tokenRefreshTime(String accessToken);
}
