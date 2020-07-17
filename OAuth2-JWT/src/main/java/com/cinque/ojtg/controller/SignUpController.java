package com.cinque.ojtg.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;

import org.apache.commons.codec.binary.Base32;
import org.hibernate.validator.constraints.NotEmpty;
import org.jboss.aerogear.security.otp.Totp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cinque.ojtg.dto.SignUpRequest;
import com.cinque.ojtg.dto.SignupResponse;
import com.cinque.ojtg.dto.UserDto;
import com.cinque.ojtg.model.User;
import com.cinque.ojtg.service.UserService;
import com.codahale.passpol.PasswordPolicy;
import com.codahale.passpol.Status;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

@RestController
public class SignUpController {

	PasswordPolicy passwordPolicy = new PasswordPolicy();

	@Autowired
	private UserService userService;

	/**
	 * 
	 * @param signUpRequest
	 * @return
	 */
	@PostMapping("/signup")
	public SignupResponse signup(@RequestBody SignUpRequest signUpRequest) {
		UserDto user = new UserDto();
		if (userService.getUserDetails(signUpRequest.getUsername()) != null) {
			return new SignupResponse(SignupResponse.Status.USERNAME_TAKEN);
		}

		Status status = passwordPolicy.check(signUpRequest.getPassword());
		if (status != Status.OK) {
			return new SignupResponse(SignupResponse.Status.WEAK_PASSWORD);
		}

		String filename = signUpRequest.getUsername()+".png";
		String secret = getSecretKey();
		String applicationName = "totp";
		user.setEmail(signUpRequest.getEmail());
		user.setFirstName(signUpRequest.getFirstName());
		user.setLastName(signUpRequest.getLastName());
		user.setPassword(signUpRequest.getPassword());
		user.setUsername(signUpRequest.getUsername());
		user.setSecret(secret);
		try {
		userService.save(user);
		String barCodeUrl = getGoogleAuthenticatorBarCode(secret, signUpRequest.getUsername(), applicationName);
		createQRCode(barCodeUrl, filename, 400, 400);
		}catch(Exception e) {
			System.out.println(e);
		}
		return new SignupResponse(SignupResponse.Status.OK, signUpRequest.getUsername(), secret);
	}
	
	/**
	 * 
	 * @param username
	 * @param code
	 * @return
	 */
	@PostMapping("/signup-confirm-secret")
	public boolean signupConfirmSecret(@RequestParam("username") String username,
			@RequestParam("code") @NotEmpty String code) {
		User user = userService.getUserDetails(username);
		try {
			if (user != null) {
				Path path = Paths.get(username+".png");
				Files.deleteIfExists(path);
				String secret = user.getSecret();
				Totp totp = new Totp(secret);
				if (totp.verify(code)) {
					return true;
				}
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
	

	/**
	 * 
	 * @param barCodeData
	 * @param filePath
	 * @param height
	 * @param width
	 * @throws WriterException
	 * @throws IOException
	 */
	private void createQRCode(String barCodeData, String filePath, int height, int width)
            throws WriterException, IOException {
        BitMatrix matrix = new MultiFormatWriter().encode(barCodeData, BarcodeFormat.QR_CODE,
                width, height);
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            MatrixToImageWriter.writeToStream(matrix, "png", out);
        }
    }

	/**
	 * 
	 * @param secret
	 * @param username
	 * @param applicationName
	 * @return
	 */
	private String getGoogleAuthenticatorBarCode(String secret, String username, String applicationName) {
		try {
            return "otpauth://totp/"
                    + URLEncoder.encode(applicationName + ":" + username, "UTF-8").replace("+", "%20")
                    + "?secret=" + URLEncoder.encode(secret, "UTF-8").replace("+", "%20")
                    + "&issuer=" + URLEncoder.encode(applicationName, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
	}

	/**
	 * 
	 * @return
	 */
	private String getSecretKey() {
		SecureRandom random = new SecureRandom();
		byte[] bytes = new byte[20];
		random.nextBytes(bytes);
		Base32 base32 = new Base32();
		return base32.encodeToString(bytes);
	}

	

}
