package com.cbw.security.jwt.admin.dto;

import lombok.Builder;

import java.util.Set;
import java.util.stream.Collectors;

import com.cbw.security.jwt.account.domain.Account;
import com.cbw.security.jwt.account.domain.Authority;

public record ResponseAdmin() {
    @Builder
    public record Info(
            String username,
            String password,
            String nickname,
            Long tokenWeight,
            Set<String> authoritySet
    ) {

        public static ResponseAdmin.Info of(Account account) {
            if(account == null) return null;

            return ResponseAdmin.Info.builder()
                    .username(account.getUsername())
                    .password(account.getPassword())
                    .nickname(account.getNickname())
                    .tokenWeight(account.getTokenWeight())
                    .authoritySet(account.getAuthorities().stream()
                            .map(Authority::getAuthorityName)
                            .collect(Collectors.toSet()))
                    .build();
        }
    }
}
