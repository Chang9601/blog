package com.whooa.blog.common.security.oauth2;

import java.io.IOException;

import java.nio.charset.StandardCharsets;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.whooa.blog.common.api.ApiResponse;
import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.common.security.jwt.JwtBundle;
import com.whooa.blog.common.security.jwt.JwtType;
import com.whooa.blog.common.security.jwt.JwtUtil;
import com.whooa.blog.user.dto.UserDto.UserResponse;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.repository.UserRepository;
import com.whooa.blog.util.CookieUtil;
import com.whooa.blog.util.SerializeDeserializeUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/* 
 * OAuth 2 인증에 성공하면 스프링 시큐리티는 SecurityConfig에 설정된 OAuth2AuthenticationSuccessHandler의 onAuthenticationSuccess() 메서드를 호출한다.
 * JWT 토큰(접근 및 새로고침)을 생성하고 각각 쿠키에 저장하며 응답을 전송한다.   
 */
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private JwtUtil jwtUtil;
	private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
	private UserRepository userRepository;

	public OAuth2AuthenticationSuccessHandler(JwtUtil jwtUtil,
			HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository,
			UserRepository userRepository) {
		this.jwtUtil = jwtUtil;
		this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
		this.userRepository = userRepository;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Authentication authentication) throws IOException, ServletException {
		Long id;
		JwtBundle jwt;
		ApiResponse<UserResponse> success;
		UserDetailsImpl userDetailsImpl;
		UserEntity userEntity;
		
		userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
		
		id = userDetailsImpl.getId();
		jwt = jwtUtil.issue(userDetailsImpl.getUsername());

		CookieUtil.set(httpServletResponse, JwtType.ACCESS_TOKEN.getType(), jwt.getAccessToken(), true, 60 * 60, "/", "Strict", false);
		CookieUtil.set(httpServletResponse, JwtType.REFRESH_TOKEN.getType(), jwt.getRefreshToken(), true, 60 * 60, "/", "Strict", false);
		
		userEntity = userRepository.findByIdAndActiveTrue(id).get();
		setRefreshToken(userEntity, jwt.getRefreshToken());
		
		clearAuthenticationAttributes(httpServletRequest, httpServletResponse);
				
		success = ApiResponse.handleSuccess(Code.OK.getCode(), Code.OK.getMessage(), toUserResponseDTO(userDetailsImpl), new String[] {"OAuth2 로그인 했습니다."});

		httpServletResponse.setStatus(HttpServletResponse.SC_OK);
		httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
		httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
		httpServletResponse.getWriter().write( SerializeDeserializeUtil.serializeToString(success));
	}
	
	protected void clearAuthenticationAttributes(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		super.clearAuthenticationAttributes(httpServletRequest);
		httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequest(httpServletRequest, httpServletResponse);
	}
	
	private UserResponse toUserResponseDTO(UserDetailsImpl userDetailsImpl) {
		UserResponse userResponse = UserResponse.builder()
										.id(userDetailsImpl.getId())
										.email(userDetailsImpl.getUsername())
										.userRole(userDetailsImpl.getUserRole())
										.build();		
		
		return userResponse;				
	}
	
	private void setRefreshToken(UserEntity userEntity, String refreshToken) {
		userEntity.setRefreshToken(refreshToken);
		userRepository.save(userEntity);
	}
}