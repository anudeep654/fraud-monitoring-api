package com.fraudguard.gateway.config;

import com.fraudguard.gateway.security.JwtAuthenticationToken;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JwtConfig jwtConfig;

    public SecurityConfig(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
            .cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource()))
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/auth/**").permitAll()
                .pathMatchers("/actuator/health").permitAll()
                .pathMatchers("/actuator/info").permitAll()
                .pathMatchers("/v1/public/**").permitAll()
                .pathMatchers("/actuator/**").hasRole("ADMIN")
                .pathMatchers("/v1/validate/**").hasAnyRole("USER", "ADMIN")
                .pathMatchers("/v1/notifications/**").hasAnyRole("USER", "ADMIN")
                .pathMatchers("/v1/model/**").hasRole("ADMIN")
                .anyExchange().authenticated())
            .addFilterAt(bearerAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
            .build();
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager() {
        return authentication -> {
            String token = authentication.getCredentials().toString();
            if (jwtConfig.validateToken(token)) {
                String username = jwtConfig.getUsernameFromToken(token);
                String[] roles = jwtConfig.getRolesFromToken(token);
                return Mono.just(new JwtAuthenticationToken(username, roles));
            }
            return Mono.error(new IllegalArgumentException("Invalid token"));
        };
    }

    @Bean
    public ServerAuthenticationConverter serverAuthenticationConverter() {
        return exchange -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                return Mono.just(new JwtAuthenticationToken(token));
            }
            return Mono.empty();
        };
    }

    @Bean
    public AuthenticationWebFilter bearerAuthenticationFilter() {
        AuthenticationWebFilter filter = new AuthenticationWebFilter(authenticationManager());
        filter.setServerAuthenticationConverter(serverAuthenticationConverter());
        return filter;
    }

    @Bean
    public ServerSecurityContextRepository securityContextRepository() {
        return new WebSessionServerSecurityContextRepository();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("https://fraudguard.com", "https://*.fraudguard.com"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setExposedHeaders(List.of("X-Total-Count"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
