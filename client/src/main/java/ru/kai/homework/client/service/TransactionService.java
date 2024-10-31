package ru.kai.homework.client.service;

import ru.kai.homework.client.model.Transaction;
import ru.kai.homework.client.model.dto.request.TransactionRequest;

import java.util.UUID;

public interface TransactionService {
    void handleTransaction(TransactionRequest dto);

    void cancelTransaction(UUID transactionId);

    Transaction retryTransaction(UUID transactionId);

    Transaction retryTransactionWithErrorHandling(UUID transactionId);
}
