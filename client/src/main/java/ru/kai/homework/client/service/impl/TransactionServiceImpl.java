package ru.kai.homework.client.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kai.homework.client.exception.InsufficientFundsException;
import ru.kai.homework.client.exception.TransactionNotFoundException;
import ru.kai.homework.client.kafka.TransactionProducer;
import ru.kai.homework.client.mapper.TransactionMapper;
import ru.kai.homework.client.model.Account;
import ru.kai.homework.client.model.Transaction;
import ru.kai.homework.client.model.dto.request.TransactionRequest;
import ru.kai.homework.client.model.enums.TransactionStatus;
import ru.kai.homework.client.model.enums.OperationType;
import ru.kai.homework.client.repository.TransactionRepository;
import ru.kai.homework.client.service.AccountService;
import ru.kai.homework.client.service.TransactionService;
import ru.kai.homework.client.utils.Operation;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionProducer transactionProducer;
    private final TransactionMapper mapper;
    private final AccountService accountService;

    @Transactional
    public void handleTransaction(TransactionRequest dto) {
        Transaction transaction = prepareTransaction(dto);
        Account account = transaction.getAccount();
        if(account.isBlocked()) {
            handleErrorTransaction(transaction);
        } else {
            try {
                processTransaction(transaction);
                transaction.setStatus(TransactionStatus.PROCESSED);
            } catch (InsufficientFundsException e) {
                handleErrorTransaction(transaction);
            }
        }
        transactionRepository.save(transaction);
    }

    @Transactional
    public void cancelTransaction(UUID transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(transactionId));
        if (transaction.getStatus() == TransactionStatus.PROCESSED) {
            Transaction compensatingTransaction = createCompensatingTransaction(transaction);
            processTransaction(compensatingTransaction);
        }
        transaction.setStatus(TransactionStatus.CANCELLED);
    }

    @Transactional
    public Transaction retryTransactionWithErrorHandling(UUID transactionId) {
        Transaction transaction = retryTransaction(transactionId);
        if (transaction.getStatus() == TransactionStatus.ERROR) {
            handleErrorTransaction(transaction);
        }
        return transaction;
    }

    @Transactional
    public Transaction retryTransaction(UUID transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(transactionId));
        Account account = transaction.getAccount();

        if (isNonRetryableStatus(transaction))
            return transaction;

        try {
            processTransaction(transaction);
            transaction.setStatus(TransactionStatus.PROCESSED);
            account.setBlocked(false);
            return transaction;
        } catch (InsufficientFundsException e) {
            transaction.setStatus(TransactionStatus.ERROR);
            return transaction;
        }
    }

    private void handleErrorTransaction(Transaction transaction) {
        transaction.setStatus(TransactionStatus.ERROR);
        transactionProducer.sendMessage(transaction);
        log.warn("Transaction failed: {}", transaction.getId());
    }

    private Transaction prepareTransaction(TransactionRequest dto) {
        Transaction transaction = mapper.toEntity(dto);
        Account account = accountService.getById(dto.getAccountId());
        transaction.setAccount(account);
        return transaction;
    }

    private void processTransaction(Transaction transaction) {
        Account account = transaction.getAccount();
        BigDecimal newBalance = Operation.calculateBalance(account.getBalance(), transaction.getAmount(), transaction.getType());
        account.setBalance(newBalance);
    }

    private Transaction createCompensatingTransaction(Transaction transaction) {
        OperationType compensatingOperation = transaction.getType() == OperationType.ACCRUAL
                ? OperationType.DEBIT
                : OperationType.ACCRUAL;
        Transaction compensatingTransaction = new Transaction();
        compensatingTransaction.setAccount(transaction.getAccount());
        compensatingTransaction.setAmount(transaction.getAmount());
        compensatingTransaction.setType(compensatingOperation);
        compensatingTransaction.setStatus(TransactionStatus.WAITING);
        return compensatingTransaction;
    }

    private boolean isNonRetryableStatus(Transaction transaction) {
        return transaction.getStatus() == TransactionStatus.CANCELLED || transaction.getStatus() == TransactionStatus.PROCESSED;
    }

}

