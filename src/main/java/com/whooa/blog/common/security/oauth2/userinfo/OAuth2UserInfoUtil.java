package com.whooa.blog.common.security.oauth2.userinfo;

import java.util.Map;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.security.exception.OAuth2AuthenticationProcessException;
import com.whooa.blog.common.security.oauth2.OAuth2Provider;

public class OAuth2UserInfoUtil {
	
    public static OAuth2UserInfo get(String registrationId, Map<String, Object> attributes) {
        if(registrationId.equalsIgnoreCase(OAuth2Provider.GOOGLE.toString())) {
            return new GoogleOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase(OAuth2Provider.NAVER.toString())) {
            return new NaverOAuth2UserInfo(attributes);
        } else {
            throw new OAuth2AuthenticationProcessException(Code.OAUTH2_INVALID_EMAIL, new String[] {"해당 소셜 로그인은 현재 지원되지 않습니다."});
        }
    }
}