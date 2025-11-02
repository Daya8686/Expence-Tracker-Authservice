package com.expencetracker.authservice.util;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.expencetracker.authservice.entities.UserInfo;
import com.expencetracker.authservice.repository.UserInfoRepository;

@Component
public class UserPrinciple {
	
	@Autowired
    private UserInfoRepository infoRepository;  // Remove static!

    public UserInfo getUserInfo() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        String username = ((CustomUserDetails) userDetails).getUsername();
        return infoRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

}
