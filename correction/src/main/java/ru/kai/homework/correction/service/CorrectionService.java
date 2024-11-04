package ru.kai.homework.correction.service;

import java.util.UUID;

public interface CorrectionService {
    void newCorrection(UUID transactionId);

    boolean authorizeTransaction(UUID transactionId);

    void retryFailedTransactions();
}

