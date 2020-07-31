package com.cinque.ojtg.config;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

//	static final String CLIENT_ID = "cinque-client";
//	static final String CLIENT_SECRET = "$2a$04$6S6jagH/COc78OIFnhCVVOGmykQz1yEwlScjq38750IU9fUmKyxRK";
//	// static final String CLIENT_SECRET = "cinque-secret";
//	static final String GRANT_TYPE_PASSWORD = "password";
//	static final String AUTHORIZATION_CODE = "authorization_code";
//	static final String REFRESH_TOKEN = "refresh_token";
//	static final String IMPLICIT = "implicit";
//	static final String SCOPE_READ = "read";
//	static final String SCOPE_WRITE = "write";
//	static final String TRUST = "trust";
//	static final int ACCESS_TOKEN_VALIDITY_SECONDS = 1 * 60 * 60;
//	static final int FREFRESH_TOKEN_VALIDITY_SECONDS = 6 * 60 * 60;

	@Autowired
	private OAuthConfig oauthConfig;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private ClientDetailsService clientDetailsService;

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		converter.setSigningKey("as466gf");
		// converter.setVerifierKey("as466gf");
		return converter;
	}

	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}

	@Bean
	@Autowired
	public TokenStoreUserApprovalHandler userApprovalHandler(TokenStore tokenStore) {
		TokenStoreUserApprovalHandler handler = new TokenStoreUserApprovalHandler();
		handler.setTokenStore(tokenStore);
		handler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));
		handler.setClientDetailsService(clientDetailsService);
		return handler;
	}

	@Bean
	@Autowired
	public ApprovalStore approvalStore(TokenStore tokenStore) throws Exception {
		TokenApprovalStore store = new TokenApprovalStore();
		store.setTokenStore(tokenStore);
		return store;
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer configurer) throws Exception {

		String CLIENT_ID = oauthConfig.getClientId().trim();
		String CLIENT_SECRET = oauthConfig.getClientSecret().trim();
		String GRANT_TYPE_PASSWORD = oauthConfig.getGrantType().trim();
		String AUTHORIZATION_CODE = oauthConfig.getAuthorizationCode().trim();
		String REFRESH_TOKEN = oauthConfig.getRefreshToken().trim();
		String IMPLICIT = oauthConfig.getImplicit().trim();
		String SCOPE_READ = oauthConfig.getScopeRead().trim();
		String SCOPE_WRITE = oauthConfig.getScopeWrite().trim();
		String TRUST = (oauthConfig.getTrust()).trim();
		int ACCESS_TOKEN_VALIDITY_SECONDS = oauthConfig.getAccessTokenValiditySeconds();
		int FREFRESH_TOKEN_VALIDITY_SECONDS = oauthConfig.getRefreshTokenValiditySeconds();

		String ENCODED_CLIENT_SECRET = passwordEncoder.encode(CLIENT_SECRET);

		configurer.inMemory().withClient(CLIENT_ID).secret(ENCODED_CLIENT_SECRET)
				.authorizedGrantTypes(GRANT_TYPE_PASSWORD, AUTHORIZATION_CODE, REFRESH_TOKEN, IMPLICIT)
				.scopes(SCOPE_READ, SCOPE_WRITE, TRUST).accessTokenValiditySeconds(ACCESS_TOKEN_VALIDITY_SECONDS)
				.refreshTokenValiditySeconds(FREFRESH_TOKEN_VALIDITY_SECONDS);
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.tokenStore(tokenStore()).authenticationManager(authenticationManager)
				.accessTokenConverter(accessTokenConverter());
		;
	}

}
