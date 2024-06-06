package com.whooa.blog.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.whooa.blog.common.security.jwt.JsonUsernamePasswordAuthenticationFilter;
import com.whooa.blog.common.security.jwt.JwtAccessDeniedHandler;
import com.whooa.blog.common.security.jwt.JwtAuthenticationEntryPoint;
import com.whooa.blog.common.security.jwt.JwtAuthenticationFilter;
import com.whooa.blog.common.security.jwt.JwtLogoutHandler;
import com.whooa.blog.common.security.jwt.JwtLogoutSuccessHandler;
import com.whooa.blog.common.security.jwt.JwtUtil;
import com.whooa.blog.user.type.UserRole;

@Configuration
public class TestSecurityConfig {

	@Bean
	public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
		JwtUtil jwtUtil = new JwtUtil();
		
		return httpSecurity
				.csrf((csrf) -> csrf.disable())
				.httpBasic((http) -> http.disable())
				.formLogin((form) -> form.disable())
				.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.logout((logout) -> logout.logoutUrl("/api/v1/auth/sign-out").addLogoutHandler(new JwtLogoutHandler()).logoutSuccessHandler(new JwtLogoutSuccessHandler()))
				.authorizeHttpRequests((authorize) ->
					authorize
							 .requestMatchers(HttpMethod.POST, "/api/v1/users/**").permitAll()
				             .requestMatchers("/api/v1/users/**").authenticated()
							 .requestMatchers(HttpMethod.GET, "/api/v1/categories/**").permitAll()
							 .requestMatchers("/api/v1/categories/**").hasAuthority(UserRole.ADMIN.getRole())
							 .requestMatchers(HttpMethod.POST, "/api/v1/**").authenticated()
							 .requestMatchers(HttpMethod.PUT, "/api/v1/**").authenticated()
							 .requestMatchers(HttpMethod.PATCH, "/api/v1/**").authenticated()
							 .requestMatchers(HttpMethod.DELETE, "/api/v1/**").authenticated()						 
							 .anyRequest().permitAll())
				.exceptionHandling((exception) -> exception.authenticationEntryPoint(new JwtAuthenticationEntryPoint()).accessDeniedHandler(new JwtAccessDeniedHandler()))
				.addFilterAt(new JsonUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(new JwtAuthenticationFilter(jwtUtil, null), JsonUsernamePasswordAuthenticationFilter.class)
				.build();
	}
}
