package com.cinque.ojtg.service;

import org.springframework.security.core.Authentication;

public interface AuthenticationFacadeService {

	Authentication getAuthentication();
}
