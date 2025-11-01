package com.expencetracker.authservice.service.impl;

import java.time.Instant;
import java.util.HashSet;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.expencetracker.authservice.entities.UserInfo;
import com.expencetracker.authservice.exceptions.UserServiceExceptionHandler;
import com.expencetracker.authservice.repository.UserInfoRepository;
import com.expencetracker.authservice.requestdto.UserInfoDto;
import com.expencetracker.authservice.requestdto.UserLoginDto;
import com.expencetracker.authservice.response.dto.AuthResponseTokensDto;
import com.expencetracker.authservice.response.dto.UserSignUpResponseDto;
import com.expencetracker.authservice.service.AuthService;
import com.expencetracker.authservice.util.ApiResponseHandler;
import com.expencetracker.authservice.util.CustomUserDetails;

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
	public ApiResponseHandler userLogin(UserLoginDto userLoginDto) {
		
		String accessToken;
		String refreshToken;
		String username = userInfoRepository.findByUsernameOrEmail(userLoginDto.getUserNameOrEmail().trim()).orElseThrow(()
				-> new UserServiceExceptionHandler("Username or Email is invalid!!", HttpStatus.BAD_REQUEST));
		Authentication auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(username, userLoginDto.getPassword()));
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

	
		

}
