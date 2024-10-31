package ru.kai.homework.client.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kai.homework.client.model.Client;
import ru.kai.homework.client.model.dto.request.ClientRequest;
import ru.kai.homework.client.service.ClientService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ClientController {
    private final ClientService clientService;

    @PostMapping("/register_client")
    public ResponseEntity<UUID> registerClient(@Valid @RequestBody ClientRequest request) {
        Client client = clientService.registerClient(request);
        return ResponseEntity.ok(client.getId());
    }
}
