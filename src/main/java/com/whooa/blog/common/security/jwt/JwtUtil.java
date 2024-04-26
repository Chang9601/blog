package com.whooa.blog.common.security.jwt;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.whooa.blog.common.type.JwtToken;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtil {
  	private static Logger logger = LoggerFactory.getLogger(JwtUtil.class);

		@Value("${spring.jwt.secret}")
		private String JWT_SECRET;
		
		public JwtToken issue(String email) {
			return new JwtToken(
					build(email, JwtExpiration.ACCESS_TOKEN_EXPIRATION.expiration), 
					build(email, JwtExpiration.REFRESH_TOKEN_EXPIRATION.expiration)
			);
		}
		
		// TODO: 재발급 구현.
		public JwtToken reissue(String refreshToken) {
			return null;
		}
		
		public boolean verify(String token) {
			try {
				Jwts.parserBuilder().setSigningKey(key()).build().parse(token);
				
				return true;
			} catch (MalformedJwtException exception) {
				logger.error("JwtUtil.validate(): 잘못된 형식의 JWT입니다.");
		  } catch (ExpiredJwtException exception) {
		  	logger.error("JwtUtil.validate(): 만료된 JWT입니다.");
		  } catch (UnsupportedJwtException exception) {
		  	logger.error("JwtUtil.validate(): 지원되지 않는 JWT입니다.");
		  } catch (SignatureException exception) {
	      logger.error("JwtUtil.validate(): 잘못된 서명입니다.");
	    } catch (IllegalArgumentException exception) {
	    	logger.error("JwtUtil.validate(): 잘못된 인자입니다.");
		  }
			
			return false;
		}
		
		public String parseEmail(String token) {
			return parseClaim(token, Claims::getSubject);
		}
		
		public String parseRequest(HttpServletRequest request) {
			String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);
			
			if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer " )) {
				return bearer.substring(7);
			}
			
			return null;
		}
		
		private String build(String email, long expiration) {
			Claims claims = Jwts.claims().setSubject(email);
			Date now = new Date();
						
			return Jwts.builder()
					  .setClaims(claims)
					  .setIssuedAt(now)
					  .setExpiration(new Date(now.getTime() + expiration))
					  .signWith(key(), SignatureAlgorithm.HS256)
					  .compact();
		}
				
		private Key key() {
			/* YAML 파일에 저장된 암호화된 문자열을 복호화한다. */
			return Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SECRET));
		}
		
		private <T> T parseClaim(String token, Function<Claims, T> claimResolver) {
			Claims claims = parseAllClaims(token);
			
			return claimResolver.apply(claims);
		}
		
		private Claims parseAllClaims(String token) {
			return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody();
		}
		
		private enum JwtExpiration {
			ACCESS_TOKEN_EXPIRATION(1000L * 60 * 60),
			REFRESH_TOKEN_EXPIRATION(1000L * 60 * 60 * 24 * 15);
			
			private JwtExpiration(long expiration) {
				this.expiration = expiration;
			}
			
			private long expiration;
		}
}