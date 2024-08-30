package com.whooa.blog.common.security.jwt;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.security.UserDetailsServiceImpl;
import com.whooa.blog.user.dto.UserDto.UserResponse;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.repository.UserRepository;
import com.whooa.blog.util.CookieUtil;
import com.whooa.blog.util.SerializeDeserializeUtil;
import com.whooa.blog.util.StringUtil;

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
	private UserDetailsServiceImpl userDetailsServiceImpl;
	private UserRepository userRepository;

	public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsServiceImpl, UserRepository userRepository) {
		this.jwtUtil = jwtUtil;
		this.userDetailsServiceImpl = userDetailsServiceImpl;
		this.userRepository = userRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
			throws ServletException, IOException {
		Cookie cookie;
		String email, jwtAccessToken, jwtRefreshToken;
		JwtBundle jwt;
		Optional<Cookie> optionalCookie;
		SecurityContext securityContext;
		ApiResponse<UserResponse> success;
		UserDetails userDetails;
		UserEntity userEntity;
		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken;
		UserResponse userResponse;
			
		optionalCookie = CookieUtil.get(httpServletRequest, JwtType.ACCESS_TOKEN.getType());
		
		if (optionalCookie.isPresent()) {
			cookie = optionalCookie.get();
			jwtAccessToken = cookie.getValue();
			
			if (StringUtil.notEmpty(jwtAccessToken) && jwtUtil.verify(jwtAccessToken)) {
				email = jwtUtil.parseEmail(jwtAccessToken);
				
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
					userDetails = userDetailsServiceImpl.loadUserByUsername(email);
					// UserResponse user = userService.findByEmail(email);
					
					/* https://docs.spring.io/spring-security/reference/servlet/authentication/architecture.html */
					securityContext = SecurityContextHolder.createEmptyContext();
					
					/* https://docs.spring.io/spring-security/reference/servlet/authentication/architecture.html */
					usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
					usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
					
					securityContext.setAuthentication(usernamePasswordAuthenticationToken);
					
					SecurityContextHolder.setContext(securityContext);
				} catch (UsernameNotFoundException exception) {
					logger.error("[JwtAuthenticationFilter] UsernameNotFoundException: {}", exception.getMessage());
				} catch (AuthenticationException exception) {
					logger.error("[JwtAuthenticationFilter] AuthenticationException: {}", exception.getMessage());
				} catch (Exception exception) {
					logger.error("[JwtAuthenticationFilter] Exception: {}", exception.getMessage());
				}
			} else if (StringUtil.notEmpty(jwtAccessToken) && !jwtUtil.verify(jwtAccessToken)) {
				optionalCookie = CookieUtil.get(httpServletRequest, JwtType.REFRESH_TOKEN.getType());
				
				if (optionalCookie.isPresent()) {
					cookie = optionalCookie.get();
					jwtRefreshToken = cookie.getValue();
					
					if (StringUtil.notEmpty(jwtRefreshToken) && jwtUtil.verify(jwtRefreshToken)) {
						email = jwtUtil.parseEmail(jwtRefreshToken);
						jwt = jwtUtil.reissue(jwtRefreshToken);
						
						CookieUtil.setJwtCookies(httpServletResponse, jwt.getAccessToken(), jwt.getRefreshToken());
						
						userEntity = userRepository.findByEmailAndActiveTrue(email).get();
						 
						userEntity.setRefreshToken(jwt.getRefreshToken());
						userRepository.save(userEntity);
						
						userResponse = new UserResponse();
						userResponse.setId(userEntity.getId());
						userResponse.setEmail(userEntity.getEmail());
						userResponse.setName(userEntity.getName());
						userResponse.setUserRole(userEntity.getUserRole());
						
						success = ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), userResponse, new String[] {"로그인 했습니다."});

						httpServletResponse.setStatus(HttpServletResponse.SC_OK);
						httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
						httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
						httpServletResponse.getWriter().write(SerializeDeserializeUtil.serializeToString(success));
						
						return;
					}
				}
			}
		}

		filterChain.doFilter(httpServletRequest, httpServletResponse);
	}
}