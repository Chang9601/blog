package com.whooa.blog.common.security.oauth2.userinfo;

import java.util.Map;

/* 
 * OAuth2 제공자는 인증된 사용자의 세부 정보를 가져올 때 서로 다른 JSON 응답을 반환한다. 
 * Spring Security는 이를 키-값 쌍의 일반적인 맵 형태로 파싱한다.
 * OAuth2UserInfo 추상 클래스는 키-값 쌍의 맵에서 사용자의 필요한 세부 정보를 가져오기 위해 사용된다.
 */
public abstract class OAuth2UserInfo {
	protected Map<String, Object> attributes;

	public OAuth2UserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	
	public abstract String getOAuth2ProviderId();
	public abstract String getName();
	public abstract String getEmail();
}