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
 * JwtAuthenticationFilter 클래스는 다음을 수행한다.
 * 1. 모든 요청의 Authorization 헤더에서 JWT 토큰을 읽는다.
 * 2. JWT 토큰의 유효성을 검사한다.
 * 3. JWT 토큰과 연결된 사용자의 정보를 로드한다.
 * 4. Spring Security의 SecurityContext에 사용자 정보를 설정한다. 
 * 
 * Spring Security는 사용자 정보를 사용하여 권한 확인을 수행한다. 
 * 컨트롤러에서 SecurityContext에 저장된 사용자 정보에 접근할 수 있다.
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
				/* 접근 토큰에서 이메일을 추출한다. */
				String email = jwtUtil.parseEmail(accessToken);
				
				/*
				 * try-catch 문이 필요한 이유.  
				 * UserDetailsService 인터페이스를 구현한 클래스에서 UsernameNotFoundException 예외가 발생할 경우 filterChain.doFilter() 메서드가 호출되지 않는다.
				 * 이에 따라서 상태 코드가 500인 오류가 발생하며 예외가 제대로 처리되지 않는다. 따라서, try-catch 문을 사용해서 다음 필터로 처리를 넘긴다.
				 * API가 permitAll() 메서드가 적용되어 있다면 ExceptionTranslationFilter 클래스가 JwtAuthFilter 클래스에서 발생한 예외를 탐지해 처리할 것이고 아닐 경우 API 컨트롤러로 요청이 전달된다.
				 * 필터 체인 과정에서 인증/인가 관련된 예외가 발생하면 ExceptionTranslationFilter 클래스가 자동으로 탐지한다.
				 */
				try {
					/*
					 * 사용자의 사용자 이름과 역할을 JWT 클레임 내에 암호화하고 해당 클레임을 JWT에서 구문 분석하여 UserDetails 객체를 생성할 수 있다.
					 * 이렇게 해서 데이터베이스 조회 쿼리를 사용할 필요가 없다.
					 */
					UserDetails userDetails = userDetailsService.loadUserByUsername(email);
				
					/*
					 * 데이터베이스에서 사용자의 정보를 로드하는 것이 유용할 수 있다. 
					 * 예를 들어, 사용자의 역할이 변경되었거나 사용자가 JWT를 생성한 후에 비밀번호를 갱신했다면 JWT로 로그인을 금지할 수 있다.
					 */
					// UserResponse user = userService.findByEmail(email);
					
					SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
					
					UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
					usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
					
					securityContext.setAuthentication(usernamePasswordAuthenticationToken);
					SecurityContextHolder.setContext(securityContext);
					
					/* 다중 스레드 상황에서 경쟁 조건이 발생할 수 있기 때문에 새로운 SecurityContext 인스턴스를 생성해야 한다. */
					// SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
				} catch (UsernameNotFoundException exception) {
					logger.error("UsernameNotFoundException: {}", exception.getMessage());
				}
			}
		}
		
		filterChain.doFilter(httpServletRequest, httpServletResponse);
	}
}