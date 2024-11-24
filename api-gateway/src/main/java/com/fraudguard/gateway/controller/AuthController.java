package com.fraudguard.gateway.controller;

import com.fraudguard.gateway.config.JwtConfig;
import com.fraudguard.gateway.model.AuthRequest;
import com.fraudguard.gateway.model.AuthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtConfig jwtConfig;
    private final PasswordEncoder passwordEncoder;
    private final Map<String, UserCredentials> userDb = new HashMap<>(); // Simulated user database

    public AuthController(JwtConfig jwtConfig, PasswordEncoder passwordEncoder) {
        this.jwtConfig = jwtConfig;
        this.passwordEncoder = passwordEncoder;
        
        // Initialize with some test users
        initializeUsers();
    }

    private void initializeUsers() {
        // Add a test admin user
        userDb.put("admin", new UserCredentials(
            passwordEncoder.encode("admin123"),
            new String[]{"ROLE_ADMIN", "ROLE_USER"}
        ));
        
        // Add a test regular user
        userDb.put("user", new UserCredentials(
            passwordEncoder.encode("user123"),
            new String[]{"ROLE_USER"}
        ));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@RequestBody AuthRequest request) {
        return Mono.just(request)
                .filter(req -> {
                    UserCredentials user = userDb.get(req.getUsername());
                    return user != null && 
                           passwordEncoder.matches(req.getPassword(), user.getPassword());
                })
                .map(req -> {
                    UserCredentials user = userDb.get(req.getUsername());
                    String token = jwtConfig.generateToken(req.getUsername(), user.getRoles());
                    return ResponseEntity.ok(new AuthResponse(token));
                })
                .defaultIfEmpty(ResponseEntity.status(401).build());
    }

    private static class UserCredentials {
        private final String password;
        private final String[] roles;

        public UserCredentials(String password, String[] roles) {
            this.password = password;
            this.roles = roles;
        }

        public String getPassword() {
            return password;
        }

        public String[] getRoles() {
            return roles;
        }
    }
}
