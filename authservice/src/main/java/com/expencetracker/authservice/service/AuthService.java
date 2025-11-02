package com.expencetracker.authservice.service;

import com.expencetracker.authservice.requestdto.AccessTokenRequestDto;
import com.expencetracker.authservice.requestdto.UserInfoDto;
import com.expencetracker.authservice.requestdto.UserLoginDto;
import com.expencetracker.authservice.util.ApiResponseHandler;

import jakarta.validation.Valid;

public interface AuthService {
	
	public ApiResponseHandler signUpUser(UserInfoDto userInfoDto);
	
	public ApiResponseHandler userLogin(UserLoginDto userLoginDto);

	public ApiResponseHandler getAccessToken(@Valid AccessTokenRequestDto accessTokenRequestDto);

	public ApiResponseHandler logoutUser();
	
	

}
