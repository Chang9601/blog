package com.whooa.blog.common.security.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * JwtAuthFilter 클래스는 다음을 수행한다.
 * 1. 모든 요청의 Authorization 헤더에서 JWT 토큰을 읽는다.
 * 2. JWT 토큰의 유효성을 검사한다.
 * 3. JWT 토큰과 연결된 사용자의 정보를 로드한다.
 * 4. Spring Security의 SecurityContext에 사용자 정보를 설정한다. 
 * 	 Spring Security는 사용자 정보를 사용하여 권한 확인을 수행한다. 
 *  컨트롤러에서 SecurityContext에 저장된 사용자 정보에 접근할 수 있다.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
	
	private JwtUtil jwtUtil;
	// private UserService userService;
	private UserDetailsService userDetailsService;
	
	public JwtAuthFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String accessToken = null;
		Cookie[] cookies = request.getCookies();
		
		if (cookies != null) {
			for (Cookie cookie: request.getCookies()) {
				String name = cookie.getName();
				
				if (name.equals("AccessToken")) {
					accessToken = cookie.getValue();
				}
			}
			
			if (accessToken != null && jwtUtil.verify(accessToken)) {
				/* JWT 토큰에서 이메일을 추출한다. */
				String email = jwtUtil.parseEmail(accessToken);
				
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
				
				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			}
		}
		
		filterChain.doFilter(request, response);
	}
}