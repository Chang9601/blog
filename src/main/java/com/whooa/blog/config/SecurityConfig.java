package com.whooa.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whooa.blog.common.security.jwt.JsonUsernamePasswordAuthFilter;
import com.whooa.blog.common.security.jwt.JwtAccessDeniedHandler;
import com.whooa.blog.common.security.jwt.JwtAuthEntryPoint;
import com.whooa.blog.common.security.jwt.JwtAuthFailureHandler;
import com.whooa.blog.common.security.jwt.JwtAuthFilter;
import com.whooa.blog.common.security.jwt.JwtAuthSuccessHandler;

/* @Configuration 어노테이션은 클래스를 Java 기반 설정 클래스로 설정하며 @Bean 어노테이션으로 Spring 빈을 설정할 수 있다. */
@Configuration
/* @EnableWebSecurity 어노테이션은 웹 보안을 활성화한다. */
@EnableWebSecurity
/* @EnableMethodSecurity 어노테이션은 어노테이션을 기반으로 메서드 수준 보안을 활성화한다. */
@EnableMethodSecurity(
	securedEnabled = true, /* @Secured 어노테이션을 활성화한다. */
	jsr250Enabled = true, /* @RolesAllowed 어노테이션을 활성화한다. */
	prePostEnabled = true /* @PreAuthorize/@PostAuthorize 어노테이션을 활성화한다. */
)
public class SecurityConfig {
	/*
	 * 엔드포인트 권한 부여 또는 인증 매니저 구성과 같은 기능에 대한 HTTP 보안을 사용자 정의할 수 있도록 WebSecurityConfigurerAdapter 클래스를 확장하는 방법을 제공했다.
	 * 그러나 최근 버전에서는 이 접근 방식을 폐기하고 컴포넌트 기반 보안 구성을 권장한다.
	 */
	
	private UserDetailsService userDetailsService;
	private ObjectMapper objectMapper;
	private JwtAuthEntryPoint jwtAuthEntryPoint;
	private JwtAccessDeniedHandler jwtAccessDeniedHandler;
	private JwtAuthSuccessHandler jwtAuthSuccessHandler;
	private JwtAuthFailureHandler jwtAuthFailureHandler;
	private JwtAuthFilter jwtAuthFilter;
	
	public SecurityConfig(UserDetailsService userDetailsService, 
						  ObjectMapper objectMapper, 
						  JwtAccessDeniedHandler jwtAccessDeniedHandler, 
						  JwtAuthEntryPoint jwtAuthEntryPoint, 
						  JwtAuthSuccessHandler jwtAuthSuccessHandler, 
						  JwtAuthFailureHandler jwtAuthFailureHandler, 
						  JwtAuthFilter jwtAuthFilter) {
		this.userDetailsService = userDetailsService;
		this.objectMapper = objectMapper;
		this.jwtAuthEntryPoint = jwtAuthEntryPoint;
		this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
		this.jwtAuthSuccessHandler = jwtAuthSuccessHandler;
		this.jwtAuthFailureHandler = jwtAuthFailureHandler;
		this.jwtAuthFilter = jwtAuthFilter;
	}
	
	@Bean
	 public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	/*
	 * AuthenticationManager 클래스는 UserDetailsService 클래스를 사용해서 데이터베이스에서 사용자를 가져온다.
	 * AuthenticationManager 클래스는 또한 PasswordEncoder 클래스를 사용해서 비밀번호를 암호화/복호화한다
	 * AuthenticationManager 클래스의 실제 구현체는 ProviderManager 클래스로 여러 AuthenticationProvider 클래스를 가진다.
	 * ProviderManager 클래스는 상황에 맞는 AuthenticationProvider 클래스에 인증을 위임한다.
	 * 
	 * 이전에는 UserDetailsService 클래스와 PasswordEncoder 클래스를 명시적으로 전달했다.
	 * Spring 5.2 또는 5.2 이상의 Spring Security에서는 자동으로 UserDetailsService 클래스와 PasswordEncoder 클래스를 AuthenticationManager 클래스에 제공한다.
	 */
	@Bean
	public AuthenticationManager authenticationManager() throws Exception {
		
		/* DaoAuthenticationProvider은 DAO에서 계정 정보를 꺼내오고 비밀번호 일치 여부를 검사하고 인증 여부를 넘긴다. */
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		
		daoAuthenticationProvider.setUserDetailsService(userDetailsService);
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		
		return new ProviderManager(daoAuthenticationProvider);
	}
	
	@Bean
	public AbstractAuthenticationProcessingFilter jsonUsernamePasswordAuthFilter() throws Exception {
		JsonUsernamePasswordAuthFilter jsonUsernamePasswordAuthFilter = new JsonUsernamePasswordAuthFilter(objectMapper);
		
		jsonUsernamePasswordAuthFilter.setAuthenticationManager(authenticationManager());
		jsonUsernamePasswordAuthFilter.setAuthenticationSuccessHandler(jwtAuthSuccessHandler);
		jsonUsernamePasswordAuthFilter.setAuthenticationFailureHandler(jwtAuthFailureHandler);
		
		return jsonUsernamePasswordAuthFilter;
	}
	
	/*
	 * @Bean 어노테이션은 Spring 컨테이너가 관리하는 빈을 생성하는 메소드를 나타낸다. 
	 * 일반적으로 구성 클래스에서 선언되어 Spring 빈 정의를 생성한다.
	 * 
	 * SecurityFilterChain 빈으로 HTTP 보안을 구성한다.
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity
			.csrf((csrf) -> csrf.disable())
			.httpBasic((http) -> http.disable())
			.formLogin((form) -> form.disable())
			.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests((authorize) -> 
				authorize.requestMatchers(HttpMethod.GET, "/api/v1/**").permitAll()
						 .requestMatchers("/api/v1/auth/**").permitAll()
						 .requestMatchers("/api/v1/users/**").permitAll()
						 .anyRequest().authenticated())
			/*
			 * 요청-응답
			 * 요청 -> 필터 -> 디스패처서블렛 -> 컨트롤러 -> 서비스 -> 레포지토리 -> 서비스 -> 컨트롤러 -> 디스패처서블렛 -> 필터 -> 응답
			 * 
			 * 컨트롤러어드바이스는 디스패처서블렛까지 요청이 전달된 후에 발생하는 예외만 처리할 수 있다.
			 * 필터는 디스패처서블렛보다 앞에 위치하기 때문에 컨트롤러어드바이스에서 이를 처리할 수 없다.
			 * 즉, 인증 중에 발생하는 AuthenticationException의 하위 클래스(e.g., UsernameNotFoundException)을 컨트롤러어드바이스가 처리할 수 없다.
			 * 따라서 Spring Security 관련 핸들러 인터페이스를 직접 구현한 다음 추가한다.
			 */
			.exceptionHandling((exception) -> exception.accessDeniedHandler(jwtAccessDeniedHandler))
			.exceptionHandling((exception) -> exception.authenticationEntryPoint(jwtAuthEntryPoint))
			/* 
			 * STATELESS는 인증 관련 세션 기능을 사용하지 않도록 하는 설정이다. 
			 * 하지만 인증 외 세션을 사용할 경우 인증 외에 세션에 관련된 필터(e.g., SessionManagementFilter, DisableEncodeUrlFilter)는 계속 필터 체인에 존재한다. 
			 * disable로 설정하면 세션 관련 설정을 아얘 하지 않기에 세션 관련 필터도 설정되지 않는다. 
			 */
			/* form 로그인 필터인 UsernamePasswordAuthenticationFilter 클래스에 JSON 로그인 필터를 추가한다. */
			.addFilterAt(jsonUsernamePasswordAuthFilter(), UsernamePasswordAuthenticationFilter.class)
			/* JSON 로그인 필터 앞에 JWT 필터를 추가한다. */
			.addFilterBefore(jwtAuthFilter, JsonUsernamePasswordAuthFilter.class)
			.build(); /* build() 메서드가 반환하는 DefaultSecurityFilterChain 클래스는 SecurityFilterChain 인터페이스의 구현 클래스이다. */
	}
	
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.debug(false);
	}

	/* 인-메모리 인증 */
//	@Bean
//	public UserDetailsService userDetailsService() {
//		UserDetails wow = User
//							.builder()
//							.username("wow")
//							.password(passwordEncoder().encode("wow"))
//							.roles("USER")
//							.build();
//		
//		UserDetails admin = User
//				.builder()
//				.username("admin")
//				.password(passwordEncoder().encode("admin"))
//				.roles("ADMIN")
//				.build();
//		
//		return new InMemoryUserDetailsManager(wow, admin);
//	}
}