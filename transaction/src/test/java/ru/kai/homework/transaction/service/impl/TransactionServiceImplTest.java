package ru.kai.homework.transaction.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.kai.homework.transaction.kafka.TransactionProducer;
import ru.kai.homework.transaction.mapper.TransactionMapper;
import ru.kai.homework.transaction.model.Transaction;
import ru.kai.homework.transaction.model.dto.request.TransactionRequest;
import ru.kai.homework.transaction.model.enums.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionMapper mapper;

    @Mock
    private TransactionProducer producer;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    void testCreateTransaction() {
        TransactionRequest request = new TransactionRequest();
        request.setType(TransactionType.DEBIT);
        request.setAmount(BigDecimal.valueOf(100.00));
        request.setAccountId(UUID.randomUUID());

        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setType(request.getType());
        transaction.setAmount(request.getAmount());
        transaction.setAccountId(request.getAccountId());

        when(mapper.toEntity(request)).thenReturn(transaction);
        Transaction result = transactionService.createTransaction(request);
        assertEquals(transaction, result);
        verify(mapper, times(1)).toEntity(request);
        verify(producer, times(1)).sendMessage(transaction);
    }
}
