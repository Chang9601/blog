package com.whooa.blog.common.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.repository.UserRepository;

/*
 * UserDetailsService는 DaoAuthenticationProvider에 의해 사용자 이름, 비밀번호 및 기타 속성을 검색하여 사용자 이름과 비밀번호로 인증하는 데 사용된다. 
 * Spring Security는 인-메모리, JDBC 및 캐싱 구현의 UserDetailsService를 제공한다.
 * 사용자 정의 UserDetailsService를 Spring 빈으로 노출하여 사용자 정의 인증을 정의할 수 있다.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
		private UserRepository userRepository;

	    public UserDetailsServiceImpl(UserRepository userRepository) {
	    	this.userRepository = userRepository;
	    }

		@Override
		public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
			UserEntity userEntity;
			
			userEntity = userRepository.findByEmailAndActiveTrue(email)
										.orElseThrow(() -> new UsernameNotFoundException("이메일과 일치하는 사용자가 존재하지 않습니다."));

			return UserDetailsImpl.create(userEntity);
		}
}