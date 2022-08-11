/**
 * 
 */
package com.flight.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * @author Vincent
 *
 */
@Service
public class JwtTokenUtil {
	public static final long JWT_VALIDITY = 60*60*5;
	
	@Value("${jwt.secret}")
	private String secret;
	
	public String generateJwt(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		return Jwts.builder().setClaims(claims).setSubject(userDetails.getUsername()).setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis() + JWT_VALIDITY*1000))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
	}
	
	public String getUsernameFromJwt(String jwt) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(jwt).getBody().getSubject();
	}
	
	public Boolean isJwtExpired(String jwt) {
		Date expiration = Jwts.parser().setSigningKey(secret).parseClaimsJws(jwt).getBody().getExpiration();
		return expiration.before(new Date());
	}
	
	public Boolean validateJwt(String jwt, UserDetails userDetails) {
		return (getUsernameFromJwt(jwt).equals(userDetails.getUsername()) && !isJwtExpired(jwt));
	}
}
