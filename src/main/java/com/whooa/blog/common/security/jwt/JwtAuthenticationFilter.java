package com.whooa.blog.common.security.jwt;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.whooa.blog.common.type.JwtType;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * OncePerRequestFilter 요청당 한 번만 호출되는 필터의 기본 클래스이다.
 * HttpServletRequest와 HttpServletResponse 매개변수를 사용하는 doFilterInternal() 메서드를 제공한다.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private static Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	private JwtUtil jwtUtil;
	// ERROR: 순환 오류 발생.
	// private UserService userService;
	private UserDetailsService userDetailsService;
	
	public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
			throws ServletException, IOException {

		String accessToken = null;
		Cookie[] cookies = httpServletRequest.getCookies();
		
		if (cookies != null) {
			for (Cookie cookie: httpServletRequest.getCookies()) {
				String name = cookie.getName();
				
				if (name.equals(JwtType.ACCESS_TOKEN.getType())) {
					accessToken = cookie.getValue();
				}
			}
			
			// TODO: verify() 메서드가 오류를 던질 경우 코드 수정 필요.
			if (accessToken != null && jwtUtil.verify(accessToken)) {
				String email = jwtUtil.parseEmail(accessToken);
				
				/*
				 * try-catch 문이 필요한 이유.  
				 * UserDetailsService 인터페이스를 구현한 클래스에서 UsernameNotFoundException 예외가 발생할 경우 filterChain.doFilter() 메서드가 호출되지 않는다.
				 * 이에 따라서 상태 코드가 500인 오류가 발생하며 예외가 제대로 처리되지 않는다. 따라서, try-catch 문을 사용해서 다음 필터로 처리를 넘긴다.
				 * API가 permitAll() 메서드가 적용되어 있다면 ExceptionTranslationFilter 클래스가 JwtAuthenticationFilter 클래스에서 발생한 예외를 탐지해 처리할 것이고 아닐 경우 API 컨트롤러로 요청이 전달된다.
				 * 필터 체인 과정에서 인증/인가 관련된 예외가 발생하면 ExceptionTranslationFilter 클래스가 자동으로 탐지한다.
				 */
				try {
					/*
					 * 사용자의 사용자 이름과 역할을 JWT 클레임 내에 암호화하고 해당 클레임을 JWT에서 구문 분석하여 UserDetails 객체를 생성할 수 있다.
					 * 이렇게 해서 데이터베이스 조회 쿼리를 사용할 필요가 없지만 데이터베이스에서 사용자의 정보를 로드하는 것이 유용할 수 있다.
					 * 예를 들어, 사용자의 역할이 변경되었거나 사용자가 JWT를 생성한 후에 비밀번호를 갱신했다면 JWT로 로그인을 금지할 수 있다.
					 */
					UserDetails userDetails = userDetailsService.loadUserByUsername(email);
					// UserResponse user = userService.findByEmail(email);
					
					/*
					 * SecurityContextHolder는 Spring Security가 인증된 사용자의 세부 정보를 저장한다. 즉, SecurityContext를 포함한다.
					 * 다중 스레드 상황에서 경쟁 조건이 발생할 수 있기 때문에 새로운 SecurityContext 인스턴스를 생성해야 한다.
					 * SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
					 * 
					 * SecurityContext는 Authentication 객체를 포함한다.
					 */
					SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
					
					/*
					 * Authentication 인터페이스의 목적.
					 * 1. AuthenticationManager의 입력으로 사용자가 인증을 위해 제공한 자격 증명을 제공하며 isAuthenticated()는 false를 반환한다. 
					 * 2. 현재 인증된 사용자를 표현하며 현재의 인증 정보를 SecurityContext에서 얻을 수 있다.
					 * 
					 * Authentication 인터페이스의 포함요소.
					 * 1. principal은 사용자를 식별하며 사용자 이름/비밀번호로 인증할 경우 대개 UserDetails 인스턴스이다.
					 * 2. credentials은 대개 비밀번호로 많은 경우 사용자가 인증된 후에는 비밀번호가 지워져서 유출되지 않도록 한다.
					 * 3. authorities는 GrantedAuthority 인스턴스는 사용자가 부여받은 고수준 권한이다. 예를 들어, 역할(roles)과 범위(scopes)가 있다.
					 * 
					 * GrantedAuthority는 principal에게 부여된 권한으로 보통 ROLE_ADMINISTRATOR나 ROLE_HR_SUPERVISOR와 같은 역할(role)이다. 
					 * 이러한 역할은 웹 권한 부여, 메서드 권한 부여 및 도메인 객체 권한 부여를 위해 나중에 설정된다. 사용자 이름/비밀번호 기반 인증을 사용할 때 GrantedAuthority 인스턴스는 보통 UserDetailsService에 의해 로드된다.
					 * 대개 GrantedAuthority 객체는 애플리케이션 전역의 권한을 나타낸다. 즉, 특정 도메인 객체에 한정되지 않는다.
					 */
					UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
					usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
					
					securityContext.setAuthentication(usernamePasswordAuthenticationToken);
					
					SecurityContextHolder.setContext(securityContext);
				} catch (UsernameNotFoundException exception) {
					logger.error("UsernameNotFoundException: {}", exception.getMessage());
				}
			}
		}
		
		filterChain.doFilter(httpServletRequest, httpServletResponse);
	}
}