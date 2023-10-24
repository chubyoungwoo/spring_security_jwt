package com.cbw.security.jwt.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/web")
public class TestController {
	
	@GetMapping("/home")
	public String home() {
		System.out.println("home");
		return "home";
	}
	
	@GetMapping("/member")
	public String member() {
		System.out.println("member");
		return "member/user";
	}

}
