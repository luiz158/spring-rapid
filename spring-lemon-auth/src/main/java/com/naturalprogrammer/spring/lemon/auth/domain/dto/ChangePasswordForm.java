package com.naturalprogrammer.spring.lemon.auth.domain.dto;

import com.naturalprogrammer.spring.lemon.auth.validation.Password;
import com.naturalprogrammer.spring.lemon.auth.validation.RetypePassword;
import com.naturalprogrammer.spring.lemon.auth.validation.RetypePasswordForm;

import lombok.*;

/**
 * Change password form.
 * 
 * @author Sanjay Patel
 */
@RetypePassword
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class ChangePasswordForm implements RetypePasswordForm {
	
	@Password
	private String oldPassword;

	@Password
	private String password;
	
	@Password
	private String retypePassword;
}
