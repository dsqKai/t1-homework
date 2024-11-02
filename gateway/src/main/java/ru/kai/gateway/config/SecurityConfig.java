package ru.kai.gateway.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.*;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Mono;
import ru.kai.gateway.util.JwtUtil;

import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
    private final JwtUtil jwtUtil;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorize -> authorize
                        .pathMatchers("/generate-token").permitAll()
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public AuthenticationWebFilter jwtAuthenticationFilter() {
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(jwtAuthenticationManager());
        authenticationWebFilter.setServerAuthenticationConverter(serverAuthenticationConverter());
        authenticationWebFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.anyExchange());
        authenticationWebFilter.setAuthenticationSuccessHandler(new WebFilterChainServerAuthenticationSuccessHandler());
        authenticationWebFilter.setAuthenticationFailureHandler(new ServerAuthenticationEntryPointFailureHandler(new HttpBasicServerAuthenticationEntryPoint()));
        return authenticationWebFilter;
    }

    @Bean
    public ServerAuthenticationConverter serverAuthenticationConverter() {
        return exchange -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String authToken = authHeader.substring(7);
                return Mono.just(new UsernamePasswordAuthenticationToken(authToken, authToken));
            } else {
                return Mono.empty();
            }
        };
    }

    @Bean
    public ReactiveAuthenticationManager jwtAuthenticationManager() {
        return authentication -> {
            log.warn("Authenticating token");
            String token = authentication.getCredentials().toString();
            return jwtUtil.validateToken(token)
                    .flatMap(isValid -> {
                        if (Boolean.TRUE.equals(isValid)) {
                            log.warn("Authentication successful");
                            // Retrieve user details if necessary
                            return Mono.just(new UsernamePasswordAuthenticationToken(null, null, Collections.emptyList()));
                        } else {
                            log.warn("Authentication failed");
                            return Mono.error(new BadCredentialsException("Invalid token"));
                        }
                    });
        };
    }
}



