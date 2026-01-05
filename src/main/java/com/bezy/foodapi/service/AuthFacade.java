package com.bezy.foodapi.service;

import org.springframework.security.core.Authentication;

public interface AuthFacade {

    Authentication getAuthentication();
}
