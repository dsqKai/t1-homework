package ru.kai.homework.client.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.kai.homework.client.model.Account;
import ru.kai.homework.client.model.dto.request.AccountRequest;
import ru.kai.homework.client.model.dto.response.MessageResponse;
import ru.kai.homework.client.service.AccountService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/register_account")
    public ResponseEntity<UUID> registerAccount(@Valid @RequestBody AccountRequest request) {
        Account account = accountService.registerAccount(request);
        return ResponseEntity.ok(account.getId());
    }

    @PostMapping("/block_account")
    public ResponseEntity<MessageResponse> blockAccount(@Valid @RequestBody UUID accountId) {
        accountService.blockAccount(accountId);
        return ResponseEntity.ok(new MessageResponse("Account blocked"));
    }
}
