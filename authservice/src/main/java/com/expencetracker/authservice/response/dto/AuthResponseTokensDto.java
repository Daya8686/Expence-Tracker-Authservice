package com.expencetracker.authservice.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseTokensDto {
	
	private String accessToken;
	
	private String refreshToken;

}
