/**
 * 
 */
package com.flight.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.flight.validators.Password;
import com.flight.validators.PasswordMatches;

/**
 * @author Vincent
 *
 */
@PasswordMatches
public class AccountDto {
	@NotBlank(message = "Username is mandatory")
	private String username;
	@NotBlank(message = "Password is mandatory")
	@Password
	private String password;
	@NotBlank(message = "Confirmed password is mandatory")
	private String confPassword;
	@NotBlank(message = "Email is mandatory")
	@Email
	private String email;
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the confPassword
	 */
	public String getConfPassword() {
		return confPassword;
	}
	/**
	 * @param confPassword the confPassword to set
	 */
	public void setConfPassword(String confPassword) {
		this.confPassword = confPassword;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @param username
	 * @param password
	 * @param confPassword
	 * @param email
	 */
	public AccountDto(String username, String password, String confPassword, String email) {
		super();
		this.username = username;
		this.password = password;
		this.confPassword = confPassword;
		this.email = email;
	}
	/**
	 * 
	 */
	public AccountDto() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
