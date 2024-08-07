package com.whooa.blog.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

	@GetMapping("/oauth2")
	public String signIn() {
		return "oauth2.html";
	}
}
