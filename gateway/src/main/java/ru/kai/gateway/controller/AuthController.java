package ru.kai.gateway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.kai.gateway.util.JwtUtil;

@RequiredArgsConstructor
@RestController
public class AuthController {
    private final JwtUtil jwtUtil;

    @GetMapping("/generate-token") // тестовая ручка в рамках домашней работы
    public Mono<String> generateToken() {
        return jwtUtil.generateToken();
    }
}
