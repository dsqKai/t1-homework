package ru.kai.homework.transaction.service;

import ru.kai.homework.transaction.model.Transaction;
import ru.kai.homework.transaction.model.dto.request.TransactionRequest;

public interface TransactionService {

    Transaction createTransaction(TransactionRequest request);
}
