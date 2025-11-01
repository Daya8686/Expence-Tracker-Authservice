package com.expencetracker.authservice.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.expencetracker.authservice.entities.UserInfo;
import com.expencetracker.authservice.exceptions.UserServiceExceptionHandler;
import com.expencetracker.authservice.repository.UserInfoRepository;
import com.expencetracker.authservice.util.CustomUserDetails;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserInfoRepository userInfoRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<UserInfo> byUsername = userInfoRepository.findByUsername(username);
		if (byUsername.isEmpty()) {
			throw new UserServiceExceptionHandler("Username is invalid!!", HttpStatus.FORBIDDEN);
		}
		return new CustomUserDetails(byUsername.get());
	}

}
