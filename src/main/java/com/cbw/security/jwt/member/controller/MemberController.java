package com.cbw.security.jwt.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cbw.security.jwt.global.dto.CommonResponse;
import com.cbw.security.jwt.member.dto.RequestMember;
import com.cbw.security.jwt.member.dto.ResponseMember;
import com.cbw.security.jwt.member.service.MemberService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // user 등록 API
    @PostMapping("/members")
    public ResponseEntity<CommonResponse> signup(
            @Valid @RequestBody RequestMember.Register registerDto
    ) {
        ResponseMember.Info userInfo = memberService.signup(registerDto);

        CommonResponse response = CommonResponse.builder()
                .success(true)
                .response(userInfo)
                .build();
        return ResponseEntity.ok(response);
    }
}