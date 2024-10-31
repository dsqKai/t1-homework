package ru.kai.homework.transaction.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.kai.homework.transaction.model.Transaction;
import ru.kai.homework.transaction.model.dto.request.TransactionRequest;
import ru.kai.homework.transaction.model.dto.response.TransactionIdResponse;
import ru.kai.homework.transaction.service.TransactionService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/transaction")
    public ResponseEntity<TransactionIdResponse> createTransaction(@Valid @RequestBody TransactionRequest request) {
        Transaction transaction = transactionService.createTransaction(request);
        TransactionIdResponse response = new TransactionIdResponse(transaction.getId());
        return ResponseEntity.ok(response);
    }
}
