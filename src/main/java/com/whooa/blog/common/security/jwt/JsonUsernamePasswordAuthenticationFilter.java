package com.whooa.blog.common.security.jwt;

import java.io.IOException;

import java.nio.charset.StandardCharsets;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;

import com.whooa.blog.util.SerializeDeserializeUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * AbstractAuthenticationProcessingFilter 클래스에서 시작하면 기본적으로 form(x-www-form-urlencoded) 로그인을 지원한다.
 * JsonUsernamePasswordAuthFilter 클래스는 JSON(application/json) 로그인을 지원한다.
 * 
 * AbstractAuthenticationProcessingFilter 클래스는는 사용자의 자격 증명을 인증하기 위한 기본 필터로 사용된다. 
 * 자격 증명이 인증되기 전에, Spring Security는 일반적으로 AuthenticationEntryPoint를 사용하여 자격 증명을 요청한다.
 * 다음으로, AbstractAuthenticationProcessingFilter는 제출된 모든 인증 요청을 인증할 수 있다.
 * 
 * 처리 과정
 * 1. 사용자가 자격 증명을 제출하면 AbstractAuthenticationProcessingFilter는 인증될 Authentication 객체를 HttpServletRequest에서 생성한다. 
 *    생성되는 Authentication의 유형은 AbstractAuthenticationProcessingFilter의 서브클래스에 따라 다르다. 
 *    예를 들어, UsernamePasswordAuthenticationFilter는 HttpServletRequest에 제출된 사용자 이름과 비밀번호를 기반으로 UsernamePasswordAuthenticationToken을 생성한다.
 * 2. 생성된 Authentication 객체는 AuthenticationManager에 전달되어 인증을 받는다.
 * 3. 인증 실패 시
 * 	  SecurityContextHolder가 지워진다.
 *    RememberMeServices.loginFail() 메서드가 호출된다. 만약 remember me가 설정되지 않으면 아무 동작도 하지 않는다.
 *    AuthenticationFailureHandler가 호출된다.
 * 4. 인증 성공 시
 *    SessionAuthenticationStrategy가 새로운 로그인을 통지받는다.
 *    Authentication 객체가 SecurityContextHolder에 설정된다. 나중에 SecurityContext를 저장하여 향후 요청에 자동으로 설정되도록 하려면 SecurityContextRepository#saveContext를 명시적으로 호출해야 한다.
 *    RememberMeServices.loginSuccess() 메서드가 호출된다. remember me가 설정되지 않았다면, 이는 아무 동작도 하지 않는다.
 *    ApplicationEventPublisher가 InteractiveAuthenticationSuccessEvent를 게시한다.
 *    AuthenticationSuccessHandler가 호출된다.
 */
public class JsonUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
	public static final String DEFAULT_JSON_USERNAME_KEY = "email";
	public static final String DEFAULT_JSON_PASSWORD_KEY = "password";
	private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/api/v1/auth/sign-in", "POST");

	private String usernameKey = DEFAULT_JSON_USERNAME_KEY;
	private String passwordKey = DEFAULT_JSON_PASSWORD_KEY;
	private boolean postOnly = true;

	public JsonUsernamePasswordAuthenticationFilter() {
		super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
	}

	/* attemptAuthentication() 메서드는 요청에서 인증 정보를 가져오고 AuthenticationManager 클래스에 인증을 위임한다. */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
			throws AuthenticationException, IOException, ServletException {
		if (this.postOnly && !httpServletRequest.getMethod().equals("POST")) {
			throw new AuthenticationServiceException("[JsonUsernamePasswordAuthenticationFilter] 지원되지 않는 인증 메서드입니다: " + httpServletRequest.getMethod());
		}
		
		if (httpServletRequest.getContentType() == null || !httpServletRequest.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)) {
			throw new AuthenticationServiceException("[JsonUsernamePasswordAuthenticationFilter] 지원되지 않는 인증 MIME 타입입니다: " + httpServletRequest.getContentType());	
		}
		
		ServletInputStream servletInputStream = httpServletRequest.getInputStream();
		SignInDto signInDto = SerializeDeserializeUtil.deserialize(StreamUtils.copyToString(servletInputStream, StandardCharsets.UTF_8), SignInDto.class);
		
		String username = signInDto.getEmail();
		String password = signInDto.getPassword();
		
		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
		
		/* Allow subclasses to set the "details" property */
		setDetails(httpServletRequest, usernamePasswordAuthenticationToken);
		
		return this.getAuthenticationManager().authenticate(usernamePasswordAuthenticationToken);
	}
	
	protected void setDetails(HttpServletRequest httpServletRequest, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
		usernamePasswordAuthenticationToken.setDetails(this.authenticationDetailsSource.buildDetails(httpServletRequest));
	}

	public void setUsernameKey(String usernameKey) {
		Assert.hasText(usernameKey, "사용자 이름 키는 반드시 존재해야 합니다.");
		this.usernameKey = usernameKey;
	}


	public void setPasswordKey(String passwordKey) {
		Assert.hasText(passwordKey, "비밀번호 키는 반드시 존재해야 합니다.");
		this.passwordKey = passwordKey;
	}

	public void setPostOnly(boolean postOnly) {
		this.postOnly = postOnly;
	}

	public String getUsernameKey() {
		return this.usernameKey;
	}

	public String getPasswordKey() {
		return this.passwordKey;
	}
	
	private static class SignInDto {
		String email;
		String password;
		
		public String getEmail() {
			return email;
		}

		public String getPassword() {
			return password;
		}

		@Override
		public String toString() {
			return "SignInDto [email=" + email + ", password=" + password + "]";
		}		
	}
}