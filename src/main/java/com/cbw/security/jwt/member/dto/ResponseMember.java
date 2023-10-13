package com.cbw.security.jwt.member.dto;

import lombok.Builder;

import java.util.Set;
import java.util.stream.Collectors;

import com.cbw.security.jwt.account.domain.Account;

public record ResponseMember() {
    @Builder
    public record Info(
            String username,
            String password,
            String nickname,
            Long tokenWeight,
            Set<String> authoritySet
    ) {

        public static ResponseMember.Info of(Account account) {
            if(account == null) return null;

            return ResponseMember.Info.builder()
                    .username(account.getUsername())
                    .password(account.getPassword())
                    .nickname(account.getNickname())
                    .tokenWeight(account.getTokenWeight())
                    .authoritySet(account.getAuthorities().stream()
                            .map(authority -> authority.getAuthorityName())
                            .collect(Collectors.toSet()))
                    .build();
        }
    }
}
