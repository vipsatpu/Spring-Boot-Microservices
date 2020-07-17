package com.cinque.ojtg.service;

public interface PasswordService {
	
	String forgotPassword(String email);
	String resetPassword(String token, String password);
}
