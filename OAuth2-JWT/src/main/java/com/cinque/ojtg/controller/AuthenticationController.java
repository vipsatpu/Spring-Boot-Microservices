package com.cinque.ojtg.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.approval.Approval.ApprovalStatus;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.cinque.common.config.ApiErrorResponse;
import com.cinque.common.config.ApiResponse;
import com.cinque.common.util.ApplicationMessageResource;
import com.cinque.ojtg.config.AuthTokenInfo;
import com.cinque.ojtg.dto.AuthenticationRequest;
import com.cinque.ojtg.dto.AuthenticationResponse;
import com.cinque.ojtg.model.User;
import com.cinque.ojtg.service.impl.Oauth2Service;

@Controller
public class AuthenticationController {

	private static final String PATH_TO_AUTHENTICATE_USER = "/authenticate";
	private static final String PATH_TO_REVOKE_TOKEN = "/revoke/token";
	private static final Logger LOG = LoggerFactory.getLogger(AuthenticationController.class);
	private final String CLASS_NAME = this.getClass().getName();

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	Oauth2Service oauth2TokenService;

	@Autowired
	ApplicationMessageResource messageResource;

	@Autowired
	private TokenStore tokenStore;

	@Autowired
	private ApprovalStore approvalStore;

	/**
	 * 
	 * @param authenticationRequest
	 * @return
	 */
	@RequestMapping(value = PATH_TO_AUTHENTICATE_USER, method = RequestMethod.POST)
	public ResponseEntity<ApiResponse> authenticate(@RequestBody @Valid AuthenticationRequest authenticationRequest) {

		AuthTokenInfo authTokenInfo = null;
		AuthenticationResponse auth_response = null;
		try {
			authTokenInfo = oauth2TokenService.getToken(authenticationRequest);
			auth_response = getUserInfo(authenticationRequest, authTokenInfo);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
							HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), e.getLocalizedMessage(), null));
		}
		return new ResponseEntity<>(auth_response, HttpStatus.OK);
	}

	@RequestMapping(value = PATH_TO_REVOKE_TOKEN, method = RequestMethod.DELETE)
	public ResponseEntity<ApiResponse> revokeToken(@RequestBody AuthTokenInfo authTokenInfo) {

		OAuth2AccessToken at = tokenStore.readAccessToken(authTokenInfo.getAccess_token());
		OAuth2RefreshToken ar = tokenStore.readRefreshToken(authTokenInfo.getRefresh_token());
		OAuth2Authentication auth = tokenStore.readAuthentication(at);
		Authentication auth_1 = auth.getUserAuthentication();
		auth_1.setAuthenticated(false);
		auth.setAuthenticated(false);
		tokenStore.removeAccessToken(at);
		tokenStore.removeRefreshToken(ar);
		SecurityContextHolder.getContext().setAuthentication(null);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * @param authenticationRequest
	 * @param auth_response
	 * @param tokenResponse
	 * @return
	 * @throws RestClientException
	 */
	private AuthenticationResponse getUserInfo(AuthenticationRequest authenticationRequest, AuthTokenInfo authTokenInfo)
			throws RestClientException {
		String username = authenticationRequest.getUsername();
		AuthenticationResponse auth_response = null;
		if (authTokenInfo != null) {
			User user = null;
			String USER_INFO_URL = "http://localhost:8080/users/userdetails/";
			USER_INFO_URL = USER_INFO_URL + username + "?access_token=" + authTokenInfo.getAccess_token();
			System.out.println(" Get User URL : " + USER_INFO_URL);
			RestTemplate restTemplate_1 = new RestTemplate();
			user = restTemplate_1.getForObject(USER_INFO_URL, User.class);
			auth_response = new AuthenticationResponse();
			auth_response.setAccessToken(authTokenInfo.getAccess_token());
			auth_response.setRefreshToken(authTokenInfo.getRefresh_token());
			auth_response.setFirstName(user.getFirstName());
			auth_response.setLastName(user.getLastName());
			auth_response.setEmail(user.getEmail());
			auth_response.setRoles(user.getRoles());
			auth_response.setId(user.getId());
		}
		return auth_response;
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public void logout(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null) {
			String tokenValue = authHeader.replace("Bearer", "").trim();
			OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);
			tokenStore.removeAccessToken(accessToken);
		}
	}

}