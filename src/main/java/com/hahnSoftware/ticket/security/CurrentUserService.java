package com.hahnSoftware.ticket.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    public JwtPrincipal requireJwtPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Not authenticated");
        }
        Object principal = auth.getPrincipal();
        if (!(principal instanceof JwtPrincipal jwt)) {
            throw new IllegalStateException("Expected JWT principal");
        }
        return jwt;
    }

    public Long requireUserId() {
        return requireJwtPrincipal().userId();
    }

    public boolean isItSupport() {
        return "IT_SUPPORT".equals(requireJwtPrincipal().role());
    }
}
