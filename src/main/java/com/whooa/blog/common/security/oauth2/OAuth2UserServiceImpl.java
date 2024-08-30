package com.whooa.blog.common.security.oauth2;

import java.util.Map;
import java.util.Optional;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.security.UserDetailsImpl;
import com.whooa.blog.common.security.exception.OAuth2AuthenticationProcessException;
import com.whooa.blog.common.security.oauth2.userinfo.OAuth2UserInfo;
import com.whooa.blog.common.security.oauth2.userinfo.OAuth2UserInfoUtil;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.repository.UserRepository;
import com.whooa.blog.util.PasswordUtil;
import com.whooa.blog.util.StringUtil;

/*
 * OAuth2UserServiceImpl는 Spring Security의 OAuth2UserService 인터페이스를 구현하며 loadUser() 메서드를 구현한다. 
 * loadUser() 메서드는 OAuth2 제공자로부터 접근 토큰을 받은 후 호출되며 먼저 OAuth2 제공자에서 사용자의 세부 정보를 가져온다.
 * 동일한 이메일을 가진 사용자가 이미 우리 데이터베이스에 존재하는 경우 해당 사용자의 정보를 수정하고 그렇지 않으면 새로운 사용자를 등록한다.
 */
@Service
public class OAuth2UserServiceImpl implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
	private UserRepository userRepository;

	public OAuth2UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
		Optional<UserEntity> optionalUserEntity;
		UserEntity userEntity;
		OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService;
		OAuth2User oAuth2User; 
		OAuth2UserInfo oAuth2UserInfo;
		String registrationId;
		
		oAuth2UserService= new DefaultOAuth2UserService();
		oAuth2User = oAuth2UserService.loadUser(oAuth2UserRequest);
		registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId().toUpperCase();
		oAuth2UserInfo = OAuth2UserInfoUtil.get(registrationId, getAttributes(registrationId, oAuth2User));
		
		if (!StringUtil.notEmpty(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessException(Code.OAUTH2_INVALID_EMAIL, new String[] {"소셜 로그인이 제공되지 않는 이메일입니다."});
		}
		
		optionalUserEntity = userRepository.findByEmailAndActiveTrue(oAuth2UserInfo.getEmail());
		
		if (optionalUserEntity.isPresent()) {
			userEntity = optionalUserEntity.get();
			
			if (!userEntity.getOAuth2Provider().equals(OAuth2Provider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId().toUpperCase()))) {
	               throw new OAuth2AuthenticationProcessException(Code.OAUTH2_LOCAL_SIGNUP, new String[] {userEntity.getOAuth2Provider() + " 이메일을 통해 이미 회원가입을 했습니다. 해당 이메일로 로그인을 하세요."});
			}
			
			userEntity = update(userEntity, oAuth2UserInfo);
		} else {
			userEntity = create(oAuth2UserRequest, oAuth2UserInfo);
		}
		
		return UserDetailsImpl.create(userEntity, oAuth2User.getAttributes());
	}
	
	private UserEntity create(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
		UserEntity userEntity;
		
		userEntity = new UserEntity();		
		userEntity.setEmail(oAuth2UserInfo.getEmail());
		userEntity.setName(oAuth2UserInfo.getName());
		userEntity.setPassword(PasswordUtil.hash(oAuth2UserInfo.getOAuth2ProviderId()));
		userEntity.setOAuth2Provider(OAuth2Provider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId().toUpperCase()));
		userEntity.setOAuth2ProviderId(oAuth2UserInfo.getOAuth2ProviderId());
		
		return userRepository.save(userEntity);
	}
	
	private UserEntity update(UserEntity userEntity, OAuth2UserInfo oAuth2UserInfo) {
		userEntity.setName(oAuth2UserInfo.getName());
		
		return userRepository.save(userEntity);
	}
	
	private Map<String, Object> getAttributes(String registrationId, OAuth2User oAuth2User) {
		switch (registrationId) {
		case "NAVER":
			return oAuth2User.getAttribute("response");
		default:
			return oAuth2User.getAttributes();
		}
	}
}