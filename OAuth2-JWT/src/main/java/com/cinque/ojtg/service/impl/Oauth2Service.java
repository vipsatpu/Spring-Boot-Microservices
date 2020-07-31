package com.cinque.ojtg.service.impl;

import java.util.LinkedHashMap;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.cinque.ojtg.config.AuthTokenInfo;
import com.cinque.ojtg.config.OAuthConfig;
import com.cinque.ojtg.dto.AuthenticationRequest;

@Service
public class Oauth2Service {

	@Autowired
	OAuthConfig oauthConfig;

	@Autowired
	RestTemplate restTemplate;

	/**
	 * 
	 * @param authenticationRequest
	 * @return oauth2Token
	 */
	public AuthTokenInfo getToken(AuthenticationRequest authenticationRequest) {
		ResponseEntity<LinkedHashMap<String, String>> tokenResponse = null;
		String credentials = oauthConfig.getClientId() + ":" + oauthConfig.getClientSecret();

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
		tokenResponse = restTemplate.exchange(oauthConfig.getAccessTokenUrl(), HttpMethod.POST, httprequest,
				parameterizedTypeReference);
		
		 AuthTokenInfo tokenInfo = null;
         
	        if(map!=null){
	            tokenInfo = new AuthTokenInfo();
	            tokenInfo.setAccess_token((String)tokenResponse.getBody().get("access_token"));
	            tokenInfo.setToken_type((String)tokenResponse.getBody().get("token_type"));
	            tokenInfo.setRefresh_token((String)tokenResponse.getBody().get("refresh_token"));
	            tokenInfo.setExpires_in(Integer.parseInt(tokenResponse.getBody().get("expires_in")));
	            tokenInfo.setScope((String)tokenResponse.getBody().get("scope"));
	            System.out.println(tokenInfo);
	            //System.out.println("access_token ="+map.get("access_token")+", token_type="+map.get("token_type")+", refresh_token="+map.get("refresh_token")
	            //+", expires_in="+map.get("expires_in")+", scope="+map.get("scope"));;
	        }else{
	            System.out.println("No user exist----------");
	             
	        }
	    return tokenInfo;
	}
	
}
