package com.whooa.blog.common.security.oauth2;

import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import com.whooa.blog.util.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * OAuth2 프로토콜은 CSRF 공격을 방지하기 위해 STATE 매개변수를 사용하는 것을 권장한다. 인증 과정에서 애플리케이션은 이 매개변수를 권한 요청에 포함시켜 보내며 OAuth2 제공자는 이 매개변수를 변경하지 않고 OAuth2 콜백에서 반환한다.
 * 애플리케이션은 OAuth2 제공자로부터 반환된 STATE 매개변수의 값과 처음에 보낸 값을 비교하며 두 값이 일치하지 않으면 인증 요청을 거부한다.
 * 애플리케이션은 나중에 OAuth2 제공자로부터 반환된 STATE와 비교할 수 있도록 STATE 매개변수를 어딘가에 저장해야 한다.
 * 다음 클래스는 STATE와 redirect_uri를 단기간 유지되는 쿠키에 저장한다.
 * 
 * AuthorizationRequestRepository는 권한 요청이 시작된 시점부터 권한 응답(콜백)이 수신될 때까지 OAuth2AuthorizationRequest를 지속적으로 관리한다.
 * OAuth2AuthorizationRequest는 권한 응답을 연관시키고 검증한다.
 * AuthorizationRequestRepository의 기본 구현은 HttpSessionOAuth2AuthorizationRequestRepository으로 OAuth2AuthorizationRequest를 HttpSession에 저장한다.
 */
@Component
public class HttpCookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
	public static final String STATE = "OAUTH2_AUTHORIZATION_REQUEST_STATE";
	private static final int expiration = 180;

	@Override
	public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest httpServletRequest) {
		return CookieUtil.get(httpServletRequest, STATE).map((cookie) -> CookieUtil.deserialize(cookie, OAuth2AuthorizationRequest.class)).orElse(null);
	}

	@Override
	public void saveAuthorizationRequest(OAuth2AuthorizationRequest oAuth2AuthorizationRequest, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		
		if (oAuth2AuthorizationRequest == null) {
			CookieUtil.clear(httpServletRequest, httpServletResponse, STATE, true, "/", "Lax", false);
			
			return;
		}
		
		/*
		 * SameSite를 Lax로 지정하지 않으면 Naver의 경우 authorization_request_not_found 오류가 발생한다.
		 * 권장 사항인 Google과 달리 Naver의 경우 state 매개변수가 필수라서 그런 것 같다.
		 */
		CookieUtil.set(httpServletResponse, STATE, CookieUtil.serialize(oAuth2AuthorizationRequest), true, expiration, "/", "Lax", false);
	}

	@Override
	public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		return this.loadAuthorizationRequest(httpServletRequest);
	}
	
	public void removeAuthorizationRequestCookie(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		CookieUtil.clear(httpServletRequest, httpServletResponse, STATE, true, "/", "Lax", false);
	}
}