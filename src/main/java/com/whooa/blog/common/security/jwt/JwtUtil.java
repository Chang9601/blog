package com.whooa.blog.common.security.jwt;

import java.security.Key;

import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.whooa.blog.common.code.Code;
import com.whooa.blog.common.security.exception.InvalidJwtRefreshTokenException;
import com.whooa.blog.common.security.exception.JwtRefreshTokenNotMatched;
import com.whooa.blog.user.entity.UserEntity;
import com.whooa.blog.user.exception.UserNotFoundException;
import com.whooa.blog.user.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JwtUtil {
  	private static Logger logger = LoggerFactory.getLogger(JwtUtil.class);

	@Value("${spring.jwt.secret}")
	private String JWT_SECRET;
	
	private UserRepository userRepository;
	
	public JwtUtil(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public JwtBundle issue(String email) {
		return new JwtBundle(
				build(email, JwtExpiration.ACCESS_TOKEN_EXPIRATION.expiration), 
				build(email, JwtExpiration.REFRESH_TOKEN_EXPIRATION.expiration)
		);
	}
	
	public JwtBundle reissue(String refreshToken) {
		String email;
		Optional<UserEntity> optionalUserEntity;
		String savedRefreshToken;
		UserEntity userEntity;

		if (!verify(refreshToken)) {
			throw new InvalidJwtRefreshTokenException(Code.INVALID_JWT_REFRESH_TOKEN, new String[] {"데이터베이스에 저장된 JWT 새로고침 토큰과 일치하지 않습니다."});
		}
			
		email = parseEmail(refreshToken);	
		optionalUserEntity = userRepository.findByEmailAndActiveTrue(email);

		if (optionalUserEntity.isEmpty()) {
			throw new UserNotFoundException(Code.NOT_FOUND, new String[] {"이메일에 해당하는 사용자가 존재하지 않습니다."});
		}
			
		userEntity = optionalUserEntity.get();
		savedRefreshToken = userEntity.getRefreshToken();
			
		/*
		 * 일치하지 않는 두 가지 경우.
		 * 1. 사용자가 재발급을 받기 전 새로고침 토큰이 이미 탈취되었고 악의적인 사용자가 먼저 재발급을 요청한 경우. 
		 *    서버에 저장된 새로고침 토큰을 삭제해 악의적인 사용자가 더 이상 접속하지 못하도록 방지한다.
		 * 2. 사용자가 기존 기기에서 로그인을 진행하고 다른 기기에서 로그인한 후에 다시 원래 기기로 돌아와서 재발급를 요청한 경우.
		 *    중복 로그인 허용할 수 있도록 새로고침 토큰을 여러개 저장하는 방법이 있다.
		 */
		if (savedRefreshToken.isEmpty() || !savedRefreshToken.equals(refreshToken)) {
			userEntity.setRefreshToken(null);
			throw new JwtRefreshTokenNotMatched(Code.JWT_REFRESH_TOKEN_NOT_MATCHED, new String[] {"데이터베이스에 저장된 JWT 새로고침 토큰과 일치하지 않습니다."});
		}
		
		JwtBundle jwt = issue(email);
		
		return jwt;
	}
	
	public boolean verify(String jwt) {
		try {
			Jwts.parserBuilder().setSigningKey(key()).build().parse(jwt);
			
			return true;
		} catch (MalformedJwtException exception) {
			logger.error("[JwtUtil]: 유효하지 않은 형식의 JWT입니다.");
		} catch (ExpiredJwtException exception) {
			logger.error("[JwtUtil]: 만료된 JWT입니다.");
		} catch (UnsupportedJwtException exception) {
			logger.error("[JwtUtil]: 지원되지 않는 JWT입니다.");
		} catch (SignatureException exception) {
			logger.error("[JwtUtil]: 유효하지 않은 서명입니다.");
		} catch (IllegalArgumentException exception) {
			logger.error("[JwtUtil]: 유효하지 않은 인자입니다.");
		}
		
		return false;
	}
	
	public String parseEmail(String jwt) {
		return parseClaim(jwt, Claims::getSubject);
	}
	
    /*  Authorinzation 헤더의 Beaer 토큰을 사용하는 경우. */
    //
	// public String parseHttpServletRequest(HttpServletRequest httpServletRequest) {
	//	 String bearer = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
	//	
	//	 if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer " )) {
	//		return bearer.substring(7);
	//	 }
	//	
	//	 return null;
	//  }
	
	private String build(String email, long expiration) {
		Claims claims = Jwts.claims().setSubject(email);
		Date now = new Date();
					
		return Jwts.builder()
				  .setClaims(claims)
				  .setExpiration(new Date(now.getTime() + expiration))
				  .setIssuedAt(now)
				  .setIssuer(email)
				  .signWith(key(), SignatureAlgorithm.HS256)
				  .compact();
	}
			
	private Key key() {
		/* YAML 파일에 저장된 암호화된 문자열을 복호화한다. */
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SECRET));
	}
	
	private <T> T parseClaim(String jwt, Function<Claims, T> claimResolver) {
		Claims claims = parseAllClaims(jwt);
		
		return claimResolver.apply(claims);
	}
	
	private Claims parseAllClaims(String jwt) {
		return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(jwt).getBody();
	}
	
	private enum JwtExpiration {
		ACCESS_TOKEN_EXPIRATION(1000L * 60 * 60),
		REFRESH_TOKEN_EXPIRATION(1000L * 60 * 60 * 24 * 30);
		
		private JwtExpiration(long expiration) {
			this.expiration = expiration;
		}
		
		private long expiration;
	}
}