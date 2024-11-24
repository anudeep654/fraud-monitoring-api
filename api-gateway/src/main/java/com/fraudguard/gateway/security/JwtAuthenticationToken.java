package com.fraudguard.gateway.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.stream.Collectors;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private final String principal;
    private String[] roles;

    public JwtAuthenticationToken(String token) {
        super(null);
        this.principal = token;
        setAuthenticated(false);
    }

    public JwtAuthenticationToken(String username, String[] roles) {
        super(Arrays.stream(roles)
                .map(role -> new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role))
                .collect(Collectors.toList()));
        this.principal = username;
        this.roles = roles;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    public String[] getRoles() {
        return roles;
    }
}
