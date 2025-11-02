package com.expencetracker.authservice.util;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.expencetracker.authservice.entities.UserInfo;

public class CustomUserDetails implements UserDetails {
	
	private String username;
	
	private String password;
	
	private Set<GrantedAuthority> authorities;

	
	public CustomUserDetails(UserInfo userInfo) {
		this.username= userInfo.getUsername();
		this.password=userInfo.getPassword();
		if(userInfo.getRoles()==null) {
			this.authorities=Set.of();
		}
		else {
		this.authorities=userInfo.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority(role.getUserRole().toUpperCase()))
				.collect(Collectors.toSet());
		}	
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		return authorities;
	}
	@Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
