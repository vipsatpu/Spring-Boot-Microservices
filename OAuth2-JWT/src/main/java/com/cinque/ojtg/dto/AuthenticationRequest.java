package com.cinque.ojtg.dto;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.stereotype.Component;

import com.cinque.common.config.ApiRequest;

@Component
public class AuthenticationRequest extends ApiRequest{

	private static final long serialVersionUID = 1L;

	@NotBlank(message = "Username can not be empty")
	private String username;
	
	@NotBlank(message = "Password can not be empty")
    private String password;
        
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
    
    
}
