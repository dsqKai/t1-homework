package ru.kai.homework.client.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
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

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionProducer transactionProducer;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private TransactionRequest transactionRequest;
    private Transaction transaction;
    private Account account;

    @BeforeEach
    void setUp() {
        transactionRequest = new TransactionRequest();
        transactionRequest.setId(UUID.randomUUID());
        transactionRequest.setType("ACCRUAL");
        transactionRequest.setAmount(new BigDecimal("100.00"));
        transactionRequest.setAccountId(UUID.randomUUID());

        account = new Account();
        account.setId(transactionRequest.getAccountId());
        account.setBalance(new BigDecimal("500.00"));
        account.setBlocked(false);

        transaction = new Transaction();
        transaction.setId(transactionRequest.getId());
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setType(OperationType.valueOf(transactionRequest.getType()));
        transaction.setAccount(account);
        transaction.setStatus(TransactionStatus.WAITING);
    }

    @Test
    void testHandleTransaction_Success() {
        when(transactionMapper.toEntity(transactionRequest)).thenReturn(transaction);
        when(accountService.getById(transactionRequest.getAccountId())).thenReturn(account);

        transactionService.handleTransaction(transactionRequest);

        verify(transactionRepository).save(transaction);
        assertEquals(TransactionStatus.PROCESSED, transaction.getStatus());
    }

    @Test
    void testHandleTransaction_AccountBlocked() {
        account.setBlocked(true);
        when(transactionMapper.toEntity(transactionRequest)).thenReturn(transaction);
        when(accountService.getById(transactionRequest.getAccountId())).thenReturn(account);

        transactionService.handleTransaction(transactionRequest);

        verify(transactionRepository).save(transaction);
        verify(transactionProducer).sendMessage(transaction);
        assertEquals(TransactionStatus.ERROR, transaction.getStatus());
    }

    @Test
    void testHandleTransaction_InsufficientFunds() {
        transaction.setType(OperationType.DEBIT);
        transactionRequest.setType("DEBIT");
        account.setBalance(new BigDecimal("50.00"));

        when(transactionMapper.toEntity(transactionRequest)).thenReturn(transaction);
        when(accountService.getById(transactionRequest.getAccountId())).thenReturn(account);

        TransactionServiceImpl spyService = Mockito.spy(transactionService);
        doThrow(new InsufficientFundsException()).when(spyService).processTransaction(any(Transaction.class));

        spyService.handleTransaction(transactionRequest);

        verify(transactionRepository).save(transaction);
        verify(transactionProducer).sendMessage(transaction);
        assertEquals(TransactionStatus.ERROR, transaction.getStatus());
    }

    @Test
    void testCancelTransaction_Success() {
        transaction.setStatus(TransactionStatus.PROCESSED);
        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));

        transactionService.cancelTransaction(transaction.getId());

        verify(transactionRepository).findById(transaction.getId());
        assertEquals(TransactionStatus.CANCELLED, transaction.getStatus());
    }

    @Test
    void testCancelTransaction_NotFound() {
        UUID transactionId = UUID.randomUUID();
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () -> {
            transactionService.cancelTransaction(transactionId);
        });
    }

    @Test
    void testRetryTransactionWithErrorHandling_Error() {
        transaction.setType(OperationType.DEBIT);
        transaction.setStatus(TransactionStatus.ERROR);
        account.setBalance(new BigDecimal("50.00"));
        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));

        Transaction result = transactionService.retryTransactionWithErrorHandling(transaction.getId());

        verify(transactionRepository).findById(transaction.getId());
        verify(transactionProducer).sendMessage(transaction);
        assertEquals(TransactionStatus.ERROR, transaction.getStatus());
        assertEquals(transaction, result);
    }

    @Test
    void testRetryTransactionWithErrorHandling_Success() {
        transaction.setStatus(TransactionStatus.ERROR);
        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));

        Transaction result = transactionService.retryTransactionWithErrorHandling(transaction.getId());

        verify(transactionRepository).findById(transaction.getId());
        assertEquals(TransactionStatus.PROCESSED, transaction.getStatus());
        assertEquals(transaction, result);
    }

    @Test
    void testRetryTransaction_NonRetryableStatus() {
        transaction.setStatus(TransactionStatus.CANCELLED);
        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));

        Transaction result = transactionService.retryTransaction(transaction.getId());

        assertEquals(transaction, result);
        verifyNoMoreInteractions(accountService);
    }

    @Test
    void testRetryTransaction_Success() {
        transaction.setStatus(TransactionStatus.ERROR);
        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));

        transactionService.retryTransaction(transaction.getId());

        assertEquals(TransactionStatus.PROCESSED, transaction.getStatus());
        assertFalse(transaction.getAccount().isBlocked());
    }

    @Test
    void testRetryTransaction_InsufficientFunds() {
        transaction.setStatus(TransactionStatus.ERROR);
        transaction.setType(OperationType.DEBIT);
        account.setBalance(new BigDecimal("50.00"));

        when(transactionRepository.findById(transaction.getId())).thenReturn(Optional.of(transaction));

        TransactionServiceImpl spyService = Mockito.spy(transactionService);
        doThrow(new InsufficientFundsException()).when(spyService).processTransaction(any(Transaction.class));

        Transaction result = spyService.retryTransaction(transaction.getId());

        assertEquals(TransactionStatus.ERROR, transaction.getStatus());
        assertEquals(transaction, result);
    }
}

