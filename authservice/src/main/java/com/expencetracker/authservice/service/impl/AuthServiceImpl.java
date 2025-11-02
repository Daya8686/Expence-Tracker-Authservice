package com.expencetracker.authservice.service.impl;

import java.time.Instant;
import java.util.HashSet;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.expencetracker.authservice.entities.RefreshToken;
import com.expencetracker.authservice.entities.UserInfo;
import com.expencetracker.authservice.exceptions.UserServiceExceptionHandler;
import com.expencetracker.authservice.repository.RefreshTokenRepository;
import com.expencetracker.authservice.repository.UserInfoRepository;
import com.expencetracker.authservice.requestdto.AccessTokenRequestDto;
import com.expencetracker.authservice.requestdto.UserInfoDto;
import com.expencetracker.authservice.requestdto.UserLoginDto;
import com.expencetracker.authservice.response.dto.AuthResponseTokensDto;
import com.expencetracker.authservice.response.dto.UserSignUpResponseDto;
import com.expencetracker.authservice.service.AuthService;
import com.expencetracker.authservice.util.ApiResponseHandler;
import com.expencetracker.authservice.util.CustomUserDetails;
import com.expencetracker.authservice.util.UserPrinciple;

import jakarta.validation.Valid;

@Service
public class AuthServiceImpl implements AuthService{
	
	@Autowired
	private UserInfoRepository userInfoRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JWTService jwtService;
	
	@Autowired
	private RefreshTokenService refreshTokenService;
	
	
	@Value("${refresh.expiration}")
	private Long refreshTokenExpiryDuration;

	@Override
	@Transactional
	@Modifying
	public ApiResponseHandler signUpUser(UserInfoDto userInfoDto) {
		userInfoDto.setEmail(userInfoDto.getEmail().trim());
		userInfoDto.setUsername(userInfoDto.getUsername().trim());
		
		if(userInfoRepository.existsByUsernameOrEmailOrMobileNo(userInfoDto.getUsername(), userInfoDto.getEmail(),userInfoDto.getMobileNo())) {
			throw new UserServiceExceptionHandler("User with this Username/Email/Mobile Number already exist!!", HttpStatus.CONFLICT);
		}
		if(!userInfoDto.getPassword().equals(userInfoDto.getConfirmPassword())) {
			throw new UserServiceExceptionHandler("Password and Confirm Password are not similar.", HttpStatus.BAD_REQUEST);
		}
		
		UserInfo userInfo = modelMapper.map(userInfoDto, UserInfo.class);
		userInfo.setRoles(new HashSet<>());
		userInfo.setPassword(passwordEncoder.encode(userInfoDto.getPassword()));
		UserInfo savedUserInfo = userInfoRepository.save(userInfo);
		UserSignUpResponseDto userSignUpResponseDto = modelMapper.map(savedUserInfo, UserSignUpResponseDto.class);
		
		return new ApiResponseHandler("Success",HttpStatus.CREATED.value(),userSignUpResponseDto, Instant.now());
	}

	@Override
	@Transactional
	public ApiResponseHandler userLogin(UserLoginDto userLoginDto) {
		
		String accessToken;
		String refreshToken;
		UserInfo userInfo= userInfoRepository.findByUsernameOrEmail(userLoginDto.getUserNameOrEmail().trim()).orElseThrow(()
				-> new UserServiceExceptionHandler("Username or Email is invalid!!", HttpStatus.BAD_REQUEST));
		Authentication auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(userInfo.getUsername(), userLoginDto.getPassword()));
		try {
			CustomUserDetails customUserDetails= (CustomUserDetails) auth.getPrincipal();
			 accessToken = jwtService.generateToken(customUserDetails.getUsername());
			 refreshToken = refreshTokenService.generateRefreshToken(customUserDetails.getUsername());

		}
		catch(BadCredentialsException badCredentialsException) {
			throw new UserServiceExceptionHandler("Password is invalid!!", HttpStatus.UNAUTHORIZED);
		}
		catch(Exception ex) {
			throw new UserServiceExceptionHandler("Invalid username or password!!", HttpStatus.UNAUTHORIZED);
		}
		AuthResponseTokensDto authResponseTokensDto = new AuthResponseTokensDto(accessToken, refreshToken);
			
		return new ApiResponseHandler("Login Success!!", HttpStatus.OK.value(), authResponseTokensDto, Instant.now());
	}

	@Override
	@Transactional
	@Modifying
	public ApiResponseHandler getAccessToken(@Valid AccessTokenRequestDto accessTokenRequestDto) {
		String refreshedAccessToken = refreshTokenService.refreshAccessToken(accessTokenRequestDto.getRefreshToken());
		return new ApiResponseHandler("Access token generated!!", 200, new AuthResponseTokensDto(refreshedAccessToken, accessTokenRequestDto.getRefreshToken()), Instant.now());
	}

	@Override
	public ApiResponseHandler logoutUser() {
		boolean revokeRefreshToken = refreshTokenService.revokeRefreshToken();
		if(!revokeRefreshToken) {
			throw new UserServiceExceptionHandler("Unable to logout user!!", HttpStatus.BAD_REQUEST);
		}
		return new ApiResponseHandler("Successfully logged out!!", 200, "User logged out!!", Instant.now()) ;
	}

	
		

}
