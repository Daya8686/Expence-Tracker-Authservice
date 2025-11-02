package com.expencetracker.authservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expencetracker.authservice.requestdto.AccessTokenRequestDto;
import com.expencetracker.authservice.requestdto.UserInfoDto;
import com.expencetracker.authservice.requestdto.UserLoginDto;
import com.expencetracker.authservice.service.AuthService;
import com.expencetracker.authservice.util.ApiResponseHandler;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
	
	@Autowired
	private AuthService authService;
	
	@PostMapping("/register")
	public ResponseEntity<ApiResponseHandler> userSignUp(@Valid @RequestBody UserInfoDto userInfoDto){
		ApiResponseHandler signUpUser = authService.signUpUser(userInfoDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(signUpUser);
	}
	
	@GetMapping("/login")
	public ResponseEntity<ApiResponseHandler> userLogin(@RequestBody UserLoginDto userLoginDto){
		ApiResponseHandler userLogin = authService.userLogin(userLoginDto);
		return ResponseEntity.status(HttpStatus.OK).body(userLogin);
	}
	
	@GetMapping("/accesstoken")
	public ResponseEntity<ApiResponseHandler> getAccessTokenByRefreshToken(@Valid @RequestBody AccessTokenRequestDto accessTokenRequestDto){
		ApiResponseHandler accessToken = authService.getAccessToken(accessTokenRequestDto);
		return ResponseEntity.status(HttpStatus.OK).body(accessToken);
	}
	
	@DeleteMapping("/logout")
	public ResponseEntity<ApiResponseHandler> logoutUser(){
		ApiResponseHandler logoutUser = authService.logoutUser();
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(logoutUser);
	}
	
	

}
