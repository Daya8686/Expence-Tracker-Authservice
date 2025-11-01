package com.expencetracker.authservice.requestdto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDto {
	
	@NotBlank(message = "Username or Email should not beleft blank!!")
	private String userNameOrEmail;
	@NotBlank(message = "Password can not be left blank!!")
	@Size(message = "Password field must be minimum of 8 characters!!", min = 8, max = 255)
	private String password;
	

}
