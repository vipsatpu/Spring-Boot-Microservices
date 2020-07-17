package com.cinque.ojtg.controller;

import java.util.LinkedHashMap;

import javax.validation.Valid;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.cinque.common.config.ApiErrorResponse;
import com.cinque.common.config.ApiResponse;
import com.cinque.common.exception.ServiceException;
import com.cinque.common.util.ApplicationMessageResource;
import com.cinque.common.util.LoggersMessageConstants;
import com.cinque.common.util.LoggersUtil;
import com.cinque.ojtg.config.OAuthConfig;
import com.cinque.ojtg.dto.AuthenticationRequest;
import com.cinque.ojtg.dto.AuthenticationResponse;
import com.cinque.ojtg.model.User;
import com.cinque.ojtg.util.MessageConstants;

@Controller
public class AuthenticationController {

	private static final String PATH_TO_AUTHENTICATE_USER = "/authenticate";
	private static String USER_INFO_URL = "http://localhost:8080/users/userdetails/";
	private static final Logger LOG = LoggerFactory.getLogger(AuthenticationController.class);

	private final String CLASS_NAME = this.getClass().getName();

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	OAuthConfig oauthConfig;
	
	@Autowired
	ApplicationMessageResource messageResource;

	/**
	 * 
	 * @param authenticationRequest
	 * @return
	 */
	@RequestMapping(value = PATH_TO_AUTHENTICATE_USER, method = RequestMethod.POST)
	public ResponseEntity<ApiResponse> authenticate(@RequestBody @Valid AuthenticationRequest authenticationRequest) {

		String actionName = CLASS_NAME + ".authenticate";
		String userName = authenticationRequest.getUsername();
		Object[] obj = new Object[1];
		obj[0] = userName;
		String logMessage = messageResource.getMessage(MessageConstants.AUTH_INIT_REQUEST, obj);
		
		LoggersUtil.info(LOG, userName, actionName, LoggersMessageConstants.SERVICE_TYPE_API,
				LoggersMessageConstants.SERVICE_STATUS_ENTERED, LoggersMessageConstants.STATUS_CODE_NULL, logMessage);
		
		ResponseEntity<LinkedHashMap<String, String>> tokenResponse = null;
		AuthenticationResponse auth_response = null;
		User user = null;
		String credentials = oauthConfig.getClientId() + ":" + oauthConfig.getClientSecret();

		try {
			String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

			// set the headers for the request
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			headers.add("Authorization", "Basic " + encodedCredentials);

			String username = authenticationRequest.getUsername();
			String password = authenticationRequest.getPassword();

			// Add form details
			MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
			map.add("username", username);
			map.add("password", password);
			map.add("grant_type", oauthConfig.getGrantType());

			ParameterizedTypeReference<LinkedHashMap<String, String>> parameterizedTypeReference = new ParameterizedTypeReference<LinkedHashMap<String, String>>() {
			};
			// Generate http request and generate access code
			HttpEntity<MultiValueMap<String, String>> httprequest = new HttpEntity<>(map, headers);
			// tokenResponse = restTemplate.exchange(ACCESS_TOKEN_URL, HttpMethod.POST,
			// httprequest, parameterizedTypeReference);
			tokenResponse = restTemplate.exchange(oauthConfig.getAccessTokenUrl(), HttpMethod.POST, httprequest,
					parameterizedTypeReference);
			if (tokenResponse != null) {
				USER_INFO_URL = USER_INFO_URL + username + "?access_token="
						+ tokenResponse.getBody().get("access_token");
				user = restTemplate.getForObject(USER_INFO_URL, User.class);
				auth_response = new AuthenticationResponse();
				auth_response.setAccessToken(tokenResponse.getBody().get("access_token"));
				auth_response.setRefreshToken(tokenResponse.getBody().get("refresh_token"));
				auth_response.setFirstName(user.getFirstName());
				auth_response.setLastName(user.getLastName());
				auth_response.setEmail(user.getEmail());
				auth_response.setRoles(user.getRoles());
				auth_response.setId(user.getId());
			}
		} catch (Exception e) {
			//return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
							e.getMessage(),e.getLocalizedMessage(), null));
		}
		return new ResponseEntity<>(auth_response, HttpStatus.OK);
	}

}