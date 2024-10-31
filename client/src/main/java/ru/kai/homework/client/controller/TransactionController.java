package ru.kai.homework.client.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.kai.homework.client.model.dto.response.MessageResponse;
import ru.kai.homework.client.service.TransactionService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/cancel_transaction")
    public ResponseEntity<MessageResponse> cancelTransaction(@Valid @RequestBody UUID id) {
        transactionService.cancelTransaction(id);
        return ResponseEntity.ok(new MessageResponse("Transaction cancelled"));
    }

    @PostMapping("/retry_transaction")
    public ResponseEntity<Void> retryTransaction(@Valid @RequestBody UUID transactionId) {
        boolean isProcessed = transactionService.retryTransaction(transactionId);
        if (isProcessed) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
