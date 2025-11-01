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
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.expencetracker.authservice.entities.RefreshToken;
import com.expencetracker.authservice.entities.UserInfo;
import com.expencetracker.authservice.exceptions.UserServiceExceptionHandler;
import com.expencetracker.authservice.repository.RefreshTokenRepository;
import com.expencetracker.authservice.repository.UserInfoRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

@Service
public class RefreshTokenService {

	@Value("${refresh.secretkey}")
	private String refreshSecretkey;
	@Value("${refresh.expiration}")
	private Long refreshExpiration;

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	@Autowired
	private UserInfoRepository userInfoRepository;

	@Autowired
	private JWTService jwtService;

	@PostConstruct
	public void init() {
		refreshSecretkey = Base64.getEncoder().encodeToString(refreshSecretkey.getBytes(StandardCharsets.UTF_8));

	}

	public String getRefreshSecretKey() {
		return refreshSecretkey;
	}

	@Transactional
	@Modifying
	public String generateRefreshToken(String username) {
		Optional<UserInfo> byUsername = userInfoRepository.findByUsername(username);
		UserInfo user = byUsername.get();
		Map<String, Object> claims = new HashMap<>();
		claims.put("username", user.getUsername());
		claims.put("tokenType", "REFRESH");
		String refreshToken = Jwts.builder().claims().add(claims).subject(user.getUsername())
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + refreshExpiration)).and().signWith(getKey())
				.compact();

		RefreshToken refresh = new RefreshToken();
		refresh.setUserInfo(user);
		refresh.setRefreshToken(refreshToken);
		refresh.setExpiryDate(new Date(System.currentTimeMillis() + refreshExpiration).toInstant());
		refreshTokenRepository.save(refresh);
		return refreshToken;

	}

	public String refreshAccessToken(String refreshToken) {
		Optional<RefreshToken> byRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken);
		if (byRefreshToken.isEmpty()) {
			throw new UserServiceExceptionHandler("Invalid refresh token!!", HttpStatus.NOT_ACCEPTABLE);
		}
		Optional<UserInfo> byUsername = userInfoRepository
				.findByUsername(byRefreshToken.get().getUserInfo().getUsername());
		UserDetails userDetails = (UserDetails) byUsername.get();
		if (validateRefreshToken(refreshToken, userDetails)) {
			return jwtService.generateToken(byUsername.get().getUsername());
		}
		refreshTokenRepository.delete(byRefreshToken.get());
		throw new UserServiceExceptionHandler("Refresh token is not valid!!! Please login again!!",
				HttpStatus.UNAUTHORIZED);
	}

	private SecretKey getKey() {
		byte[] keyBytes = Decoders.BASE64.decode(refreshSecretkey);
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

	public boolean validateRefreshToken(String token, UserDetails userDetails) {
		final String userName = extractUserName(token);
		return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token) && isRefreshToken(token));
	}

	public boolean isRefreshToken(String token) {
		String refreshToken = extractClaim(token, claims -> claims.get("tokenType", String.class));
		return refreshToken.equals("REFRESH");
	}

	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

}
