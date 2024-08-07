package com.whooa.blog.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.whooa.blog.common.security.AccessDeniedHandlerImpl;
import com.whooa.blog.common.security.AuthenticationEntryPointImpl;
import com.whooa.blog.common.security.AuthenticationFailureHandlerImpl;
import com.whooa.blog.common.security.AuthenticationSuccessHandlerImpl;
import com.whooa.blog.common.security.JsonUsernamePasswordAuthenticationFilter;
import com.whooa.blog.common.security.LogoutHandlerImpl;
import com.whooa.blog.common.security.LogoutSuccessHandlerImpl;
import com.whooa.blog.common.security.UserDetailsServiceImpl;
import com.whooa.blog.common.security.jwt.JwtAuthenticationFilter;
import com.whooa.blog.common.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.whooa.blog.common.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.whooa.blog.common.security.oauth2.OAuth2AuthenticationSuccessHandler;
import com.whooa.blog.common.security.oauth2.OAuth2UserServiceImpl;
import com.whooa.blog.user.type.UserRole;

import jakarta.servlet.DispatcherType;

/* @Configuration 어노테이션은 클래스를 Java 기반 설정 클래스로 설정하며 @Bean 어노테이션으로 Spring 빈을 설정할 수 있다. */
@Configuration
/* @EnableWebSecurity 어노테이션은 요청 수준 보안을 활성화한다. */
@EnableWebSecurity
/* @EnableMethodSecurity 어노테이션은 메서드 수준 보안을 활성화한다. */
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
	private AccessDeniedHandlerImpl accessDeniedHandlerImpl;
	private AuthenticationEntryPointImpl authenticationEntryPointImpl;
	private AuthenticationFailureHandlerImpl authenticationFailureHandlerImpl;
	private AuthenticationSuccessHandlerImpl authenticationSuccessHandlerImpl;
	private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
	private JwtAuthenticationFilter jwtAuthenticationFilter;
	private LogoutHandlerImpl logoutHandlerImpl;
	private LogoutSuccessHandlerImpl logoutSuccessHandlerImpl;
	private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
	private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
	private OAuth2UserServiceImpl oAuth2UserServiceImpl;
	private UserDetailsServiceImpl userDetailsServiceImpl;

	public SecurityConfig(AccessDeniedHandlerImpl accessDeniedHandlerImpl, AuthenticationEntryPointImpl authenticationEntryPointImpl,
			AuthenticationFailureHandlerImpl authenticationFailureHandlerImpl,AuthenticationSuccessHandlerImpl authenticationSuccessHandlerImpl,
			HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository, JwtAuthenticationFilter jwtAuthenticationFilter,
			LogoutHandlerImpl logoutHandlerImpl, LogoutSuccessHandlerImpl logoutSuccessHandlerImpl,
			OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler, OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler,
			UserDetailsServiceImpl userDetailsServiceImpl, OAuth2UserServiceImpl oAuth2UserServiceImpl) {
		this.accessDeniedHandlerImpl = accessDeniedHandlerImpl;
		this.authenticationEntryPointImpl = authenticationEntryPointImpl;
		this.authenticationFailureHandlerImpl = authenticationFailureHandlerImpl;
		this.authenticationSuccessHandlerImpl = authenticationSuccessHandlerImpl;
		this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.logoutHandlerImpl = logoutHandlerImpl;
		this.logoutSuccessHandlerImpl = logoutSuccessHandlerImpl;
		this.oAuth2AuthenticationFailureHandler = oAuth2AuthenticationFailureHandler;
		this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
		this.oAuth2UserServiceImpl = oAuth2UserServiceImpl;
		this.userDetailsServiceImpl = userDetailsServiceImpl;
	}
	
	/*
	 * Spring Security의 서블릿 지원에는 PasswordEncoder와 통합하여 비밀번호를 안전하게 저장하는 기능이 포함된다. 
	 * PasswordEncoder 빈을 노출하여 Spring Security에서 사용하는 PasswordEncoder 구현을 사용자 정의할 수 있다. 
	 */
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/*
	 * AuthenticationManager는 Spring Security의 필터들이 인증을 수행하는 방식을 정의하는 API이다. 
	 * AuthenticationManager가 반환하는 Authentication 객체는 이를 호출한 컨트롤러(즉, Spring Security의 필터 인스턴스들)에 의해 SecurityContextHolder에 설정됩된다. 
	 * Spring Security의 필터 인스턴스와 통합하지 않는 경우 직접 SecurityContextHolder를 설정할 수 있으며, AuthenticationManager를 사용할 필요가 없다.
	 * AuthenticationManager의 구현체는 다양한 형태일 수 있지만 가장 일반적인 구현체는 ProviderManager이다.
	 * 
	 * AuthenticationProvider는 ProviderManager에 의해 특정 유형의 인증을 수행하는 데 사용된다.
	 * ProviderManager에 여러 AuthenticationProvider 인스턴스를 주입할 수 있다. 각 AuthenticationProvider는 특정 유형의 인증을 수행한다. 
	 * 예를 들어, DaoAuthenticationProvider는 사용자 이름/비밀번호 기반 인증을 지원하고 JwtAuthenticationProvider는 JWT 토큰 인증을 지원한다.
	 *  
	 * Spring 5.2 또는 5.2 이상의 Spring Security에서는 자동으로 UserDetailsService 클래스와 PasswordEncoder 클래스를 AuthenticationManager 클래스에 제공한다.
	 * 이전에는 UserDetailsService 클래스와 PasswordEncoder 클래스를 명시적으로 전달했다.
	 */
	@Bean
	public AuthenticationManager authenticationManager() throws Exception {
		/*
		 * DaoAuthenticationProvider는 UserDetailsService와 PasswordEncoder를 사용하여 사용자 이름과 비밀번호를 인증하는 AuthenticationProvider 구현이다. 
		 *
		 * 작동과정
		 * 1. 인증 필터는 UsernamePasswordAuthenticationToken을 AuthenticationManager(ProviderManager에 의해 구현)로 전달한다.
		 * 2. ProviderManager는 DaoAuthenticationProvider 유형의 AuthenticationProvider를 사용하도록 구성된다.
		 * 3. DaoAuthenticationProvider는 UserDetailsService에서 UserDetails를 조회한다.
		 * 4. DaoAuthenticationProvider는 이전 단계에서 반환된 UserDetails의 비밀번호를 PasswordEncoder를 사용하여 검증한다.
		 * 5. 인증이 성공하면 반환된 Authentication은 UsernamePasswordAuthenticationToken 유형이며 주체(principal)는 구성된 UserDetailsService가 반환한 UserDetails이다. 
		 *    최종적으로 반환된 UsernamePasswordAuthenticationToken은 인증 필터에 의해 SecurityContextHolder에 설정된다.
		 */
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		
		daoAuthenticationProvider.setUserDetailsService(userDetailsServiceImpl);
		daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());
		
		return new ProviderManager(daoAuthenticationProvider);
	}
	
	@Bean
	public JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordAuthenticationFilter() throws Exception {
		JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordAuthFilter = new JsonUsernamePasswordAuthenticationFilter();
		
		jsonUsernamePasswordAuthFilter.setAuthenticationManager(authenticationManager());
		jsonUsernamePasswordAuthFilter.setAuthenticationSuccessHandler(authenticationSuccessHandlerImpl);
		jsonUsernamePasswordAuthFilter.setAuthenticationFailureHandler(authenticationFailureHandlerImpl);
		
		return jsonUsernamePasswordAuthFilter;
	}
	
	/*
	 * Spring 빈으로 등록되면 자동으로 AuAuthorityAuthorizationManager에게 전파된다.
	 */
	 @Bean
	 public static RoleHierarchy roleHierarchy() {
	    RoleHierarchyImpl roleHierarchyImpl = new RoleHierarchyImpl();
	    Map<String, List<String>> roleHierarchyMap = new HashMap<>();
	    
	    roleHierarchyMap.put("ADMIN", List.of("USER"));
	    
	    String roleHierarchyFromMap = RoleHierarchyUtils.roleHierarchyFromMap(roleHierarchyMap);
	    roleHierarchyImpl.setHierarchy(roleHierarchyFromMap);
	    
	    return roleHierarchyImpl;
	}
	 
	 @Bean
	 public static MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
	 	DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
	 	expressionHandler.setRoleHierarchy(roleHierarchy);
	 	
	 	return expressionHandler;
	 }
	 
	 /*
	  * 기본적으로 Spring OAuth2는 HttpSessionOAuth2AuthorizationRequestRepository를 사용하여 인가 요청을 저장한다. 
	  * 하지만, JWT를 사용하는 무상태이기 때문에 세션에 이를 저장할 수 없다. 따라서 Base64로 부호화된 쿠키에 요청을 저장한다.
	  * 
	  */
//	 @Bean
//	 public HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository() {
//		 return new HttpCookieOAuth2AuthorizationRequestRepository();
//	 }
	
	/*
	 * @Bean 어노테이션은 Spring 컨테이너가 관리하는 빈을 생성하는 메소드를 나타낸다. 
	 * 일반적으로 구성 클래스에서 선언되어 Spring 빈 정의를 생성한다.
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {		
		return httpSecurity
			.csrf((csrf) -> csrf.disable())
			.httpBasic((http) -> http.disable())
			.formLogin((form) -> form.disable())
			/* 
			 * STATELESS는 인증 관련 세션 기능을 사용하지 않도록 하는 설정이다. 
			 * 하지만 인증 외 세션을 사용할 경우 인증 외에 세션에 관련된 필터(e.g., SessionManagementFilter, DisableEncodeUrlFilter)는 계속 필터 체인에 존재한다. 
			 * disable로 설정하면 세션 관련 설정을 아얘 하지 않기에 세션 관련 필터도 설정되지 않는다. 
			 */
			.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			// TODO: 로그인과 로그아웃 URL은 예외로 작동한다. 따로 필터를 만들어야 하나?
			.logout((logout) -> logout.logoutUrl("/api/v1/auth/sign-out").addLogoutHandler(logoutHandlerImpl).logoutSuccessHandler(logoutSuccessHandlerImpl))
			.authorizeHttpRequests((authorize) -> 
				authorize
						.dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
						 /*
						  * permitAll() 메서드의 의미.
						  * 모든 필터 체인을 거쳤는데 인증 객체를 담는 SecurityContext 클래스에 인증 객체가 존재하지 않으면 해당 요청이 인증되지 않았음을 의미한다.
						  * 그러나, 만약 해당 API에 permitAll()을 적용하면 SecurityContext 클래스에 인증 객체가 존재 여부와 상관없이 API 호출이 이루어진다.
						  * 즉, permitAll() 메서드 적용 시 필터 체인 동작 과정에서 인증/인가 예외가 발생해도 ExceptionTranslationFilter 클래스를 거치지 않는다. 
						  * 인증 객체 존재 여부와 상관없이 정상적으로 API 호출이 이루어진다.
						  */
					     // TODO: 로그아웃 URL은 인증/인가가 적용이 안된다. 
						 .requestMatchers("/api/v1/auth/sign-out").hasAuthority(UserRole.USER.getRole())
						 .requestMatchers(HttpMethod.POST, "/api/v1/users/**").permitAll()
			             .requestMatchers("/api/v1/users/**").hasAuthority(UserRole.USER.getRole())//.authenticated()
						 .requestMatchers(HttpMethod.GET, "/api/v1/categories/**").permitAll()
						 .requestMatchers("/api/v1/categories/**").hasAuthority(UserRole.ADMIN.getRole())
						 /* 
						  * hasRole() 메서드는 hasAuthority() 메서드와 동일하지만 차이점은 다음과 같다. 
						  * hasRole('ADMIN') -> 열거형은 ROLE_ADMIN.
						  * hasAuthority('ADMIN') -> 열거형은 ADMIN.
						  */
						 .requestMatchers(HttpMethod.POST, "/api/v1/**").authenticated()
						 .requestMatchers(HttpMethod.PUT, "/api/v1/**").authenticated()
						 .requestMatchers(HttpMethod.PATCH, "/api/v1/**").authenticated()
						 .requestMatchers(HttpMethod.DELETE, "/api/v1/**").authenticated()						 
						 .anyRequest().permitAll())
			.oauth2Login((oauth2) -> oauth2
										.authorizationEndpoint((authorization) -> authorization.baseUri("/api/v1/oauth2/authorization")
										.authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository))
										.redirectionEndpoint((redirection) -> redirection.baseUri("/api/v1/oauth2/code/*"))
										.userInfoEndpoint((userInfo) -> userInfo.userService(oAuth2UserServiceImpl))
										.successHandler(oAuth2AuthenticationSuccessHandler)
										.failureHandler(oAuth2AuthenticationFailureHandler))
			/*
			 * 요청-응답
			 * 요청 -> 필터 -> 디스패처서블렛 -> 컨트롤러 -> 서비스 -> 레포지토리 -> 서비스 -> 컨트롤러 -> 디스패처서블렛 -> 필터 -> 응답
			 * 
			 * 컨트롤러어드바이스는 디스패처서블렛까지 요청이 전달된 후에 발생하는 예외만 처리할 수 있다.
			 * 필터는 디스패처서블렛보다 앞에 위치하기 때문에 컨트롤러어드바이스에서 이를 처리할 수 없다.
			 * 즉, 인증 중에 발생하는 AuthenticationException의 하위 클래스(e.g., UsernameNotFoundException)을 컨트롤러어드바이스가 처리할 수 없다.
			 * 따라서 Spring Security 관련 핸들러 인터페이스를 직접 구현한 다음 추가한다.
			 */
			.exceptionHandling((exception) -> exception.authenticationEntryPoint(authenticationEntryPointImpl).accessDeniedHandler(accessDeniedHandlerImpl))
			.addFilterAt(jsonUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(jwtAuthenticationFilter, JsonUsernamePasswordAuthenticationFilter.class)
			.build();
	}
	
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.debug(false);
	}
}