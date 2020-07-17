package com.cinque.ojtg.service.impl;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.cinque.ojtg.dao.UserDao;
import com.cinque.ojtg.model.User;
import com.cinque.ojtg.service.PasswordService;

@Service("passwordService")
public class PasswordServiceImpl implements PasswordService {

	private static final long EXPIRE_TOKEN_AFTER_MINUTES = 15;
	private String emailText = "Your Request for the password change is under process. Code will be valid for 15 Minutes. "
			+ "The code for password change is : ";
	private final String PASSWORD = "ENTER YOUR GMAIL PASSWORD HERE";
	@Autowired
	private UserDao userDao;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Override
	public String forgotPassword(String email) {

		User user = userDao.findByEmail(email);

		if (user == null) {
			return "Invalid email id.";
		}
		String token = generateToken();
		user.setResetToken(token);
		user.setTokenCreationDate(LocalDateTime.now());
		user = userDao.save(user);
		try {
			sendmail(user.getEmail(),emailText + user.getResetToken());
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Email Sent.";
	}

	@Override
	public String resetPassword(String token, String password) {

		User user = userDao.findByResetToken(token);
		if (user == null) {
			return "Invalid token.";
		}
		LocalDateTime tokenCreationDate = user.getTokenCreationDate();
		if (isTokenExpired(tokenCreationDate)) {
			user.setResetToken(null);
			user.setTokenCreationDate(null);
			return "Token expired.";
		}
		user.setPassword(passwordEncoder.encode(password));
		user.setResetToken(null);
		user.setTokenCreationDate(null);
		userDao.save(user);
		return "Your password successfully updated.";
	}

	private String generateToken() {
		StringBuilder token = new StringBuilder();

		return token.append(UUID.randomUUID().toString()).append(UUID.randomUUID().toString()).toString();
	}

	private boolean isTokenExpired(final LocalDateTime tokenCreationDate) {

		LocalDateTime now = LocalDateTime.now();
		Duration diff = Duration.between(tokenCreationDate, now);

		return diff.toMinutes() >= EXPIRE_TOKEN_AFTER_MINUTES;
	}
	
	private void sendmail(String email, String content) throws AddressException, MessagingException, IOException {
		   Properties props = new Properties();
		   props.put("mail.smtp.auth", "true");
		   props.put("mail.smtp.starttls.enable", "true");
		   props.put("mail.smtp.host", "smtp.gmail.com");
		   props.put("mail.smtp.port", "587");
		   
		   Session session = Session.getInstance(props, new javax.mail.Authenticator() {
		      protected PasswordAuthentication getPasswordAuthentication() {
		         return new PasswordAuthentication("ENTER YOUR EMAIL HERE", PASSWORD);
		      }
		   });
		   Message msg = new MimeMessage(session);
		   msg.setFrom(new InternetAddress("noreply@gmail.com", false));

		   msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
		   msg.setSubject("Password Reset Request");
		   msg.setContent(content, "text/html");
		   msg.setSentDate(new Date());

		   MimeBodyPart messageBodyPart = new MimeBodyPart();
		   messageBodyPart.setContent(content, "text/html");
		   Transport.send(msg);   
		}
	
}
