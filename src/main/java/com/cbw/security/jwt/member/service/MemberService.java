package com.cbw.security.jwt.member.service;

import com.cbw.security.jwt.member.dto.RequestMember;
import com.cbw.security.jwt.member.dto.ResponseMember;

public interface MemberService {
    ResponseMember.Info signup(RequestMember.Register registerDto);
    ResponseMember.Info getUserWithAuthorities(String username);
    ResponseMember.Info getMyUserWithAuthorities();
}
