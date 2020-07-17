package com.cinque.ojtg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cinque.ojtg.service.PasswordService;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
public class ForgotPasswordController {

	@Autowired
	PasswordService passwordService;

	@PostMapping("/forgot-password")
	public ResponseEntity<?> forgotPassword(@RequestParam String email) {

		String response = passwordService.forgotPassword(email);

		return new ResponseEntity<>(response,HttpStatus.OK);
	}

	@PutMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@RequestBody ObjectNode request) {
		String result;
		String code = request.get("code").asText();
		String password = request.get("password").asText();
		if (request.has("code") && request.has("password")) {
			result = passwordService.resetPassword(code, password);
			return new ResponseEntity<>(result,HttpStatus.OK);
		}
		return new ResponseEntity<>("Invalid request",HttpStatus.BAD_REQUEST);
	}
}
