package com.cinque.ojtg.service.impl;

import com.cinque.ojtg.service.AuthenticationFacadeService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFacadeServiceImpl implements AuthenticationFacadeService {

	@Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
