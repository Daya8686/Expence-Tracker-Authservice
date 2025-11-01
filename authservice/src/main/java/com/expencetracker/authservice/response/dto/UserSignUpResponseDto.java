package com.expencetracker.authservice.response.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSignUpResponseDto {
	
	private UUID userId;
	
	private String username;
	
	private String email;

}
