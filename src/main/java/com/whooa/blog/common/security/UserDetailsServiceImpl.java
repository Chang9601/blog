package com.whooa.blog.common.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.repository.UserRepository;

/*
 * 사용자를 인증하거나 다양한 역할 기반 확인을 수행하기 위해 Spring Security는 사용자 정보를 로드하는 방법을 결정해야 한다.
 * UserDetailsService 인터페이스는 사용자 이름을 기반으로 사용자를 로드하는 메서드를 가진다.
 * UserDetailService 클래스는 UserDetailsService 인터페이스의 loadUserByUsername() 메서드를 구현한다.
 * loadUserByUsername() 메서드는 Spring Security가 다양한 인증 및 역할 기반 유효성 검사를 수행하는 데 사용하는 UserDetails 객체를 반환한다.
 * UserDetailService 클래스는 loadUserByUsername() 메서드에서 UserDetail 클래스를 반환한다.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
		private UserRepository userRepository;

	    public UserDetailsServiceImpl(UserRepository userRepository) {
	    	this.userRepository = userRepository;
	    }

		@Override
		public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
			UserEntity userEntity = userRepository.findByEmail(email)
												.orElseThrow(() -> new UsernameNotFoundException("이메일과 일치하는 사용자가 존재하지 않습니다."));

			return new UserDetailsImpl(userEntity);
		}
}