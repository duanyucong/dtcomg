package com.dtcomg.system.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service("ss")
public class SecurityService {

    /**
     * Check if the current user has the specified permission.
     *
     * @param permission The permission string to check.
     * @return true if the user has the permission, false otherwise.
     */
    public boolean hasPermi(String permission) {
        if (permission == null || permission.trim().isEmpty()) {
            return false;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities == null) {
            return false;
        }
        // Check if any authority matches the given permission string.
        return authorities.stream().anyMatch(grantedAuthority -> permission.equals(grantedAuthority.getAuthority()));
    }
}
