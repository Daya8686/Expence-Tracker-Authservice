package com.expencetracker.authservice.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.expencetracker.authservice.entities.UserInfo;
import com.expencetracker.authservice.repository.UserInfoRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Service
public class JWTService {

	@Value("${jwt.secretkey}")
	private String secretkey;
	@Value("${jwt.expiration}")
	private Long jwtExpiration;
	
	@Autowired
	private UserInfoRepository userInfoRepository;

	@PostConstruct
	public void init() {
		secretkey = Base64.getEncoder().encodeToString(secretkey.getBytes(StandardCharsets.UTF_8));

	}

	public String getSecretKey() {
		return secretkey;
	}

	public String generateToken(String username) {
		Optional<UserInfo> userInfo = userInfoRepository.findByUsername(username);
		UserInfo user= userInfo.get();
		Map<String, Object> claims = new HashMap<>();
		claims.put("userId", user.getUserId());
		claims.put("role", user.getRoles());
		claims.put("tokenType", "ACCESS");
		return Jwts.builder()
				.claims()
				.add(claims)
				.subject(user.getUsername())
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + jwtExpiration))
				.and()
				.signWith(getKey())
				.compact();
		
	}

	private SecretKey getKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretkey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String extractUserName(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
		final Claims claims = extractAllClaims(token);
		return claimResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
	}

	public boolean validateToken(String token, UserDetails userDetails) {
		final String userName = extractUserName(token);
		return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token) && isAccessToken(token));
	}

	public boolean isAccessToken(String token) {
		String accessToken = extractClaim(token, claims -> claims.get("tokenType", String.class));
		return accessToken.equals("ACCESS");
	}

	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

}
