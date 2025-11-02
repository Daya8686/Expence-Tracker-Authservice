package com.expencetracker.authservice.requestdto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDto {
	
	@NotBlank
	@Size(message = "Username must contains Min 4 characters and Max 254 characters", min = 4, max = 50)
	private String username;
	
	@NotBlank
	@Size(message = "First Name must contains Min 4 characters and Max 254 characters", min = 4, max = 100)
	private String firstName;
	@NotBlank
	@Size(message = "Last Name must contains Min 4 characters and Max 254 characters", min = 4, max = 100)
	private String lastName;
	@Email(
	        regexp = "^(?=[A-Z0-9][A-Z0-9._%+-]{0,63}@)([A-Z0-9._%+-]{1,64}@)(?:(?:[A-Z0-9](?:[A-Z0-9-]{0,61}[A-Z0-9])?\\.)+[A-Z]{2,63})$",
	        flags = Pattern.Flag.CASE_INSENSITIVE,
	        message = "Invalid email format"
	    )
	@NotBlank
	@Size(message = "Email field must not be left empty!", min = 9, max = 254)
	private String email;
	
	
	@Pattern(
			regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}$",
		    message = "Password must be at least 8 characters long and contain at least one uppercase letter, one digit, and one special character"
		)
	@NotBlank
	@Size(message = "Password field must not be left empty!", min = 8, max = 255)
		private String password;
	@Pattern(
			regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,}$",
		    message = "Confirm Password must be at least 8 characters long and contain at least one uppercase letter, one digit, and one special character"
		)
	@NotBlank
	@Size(message = "Confirm Password field must not be left empty!", min = 8, max = 255)
		private String confirmPassword;
	
	@Pattern(
		    regexp = "^\\d{10}$",
		    message = "Mobile number must be exactly 10 digits long and contain only numbers"
		)
	@NotBlank
	private String mobileNo;
	

}
