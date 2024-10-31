package ru.kai.homework.transaction.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kai.homework.transaction.kafka.TransactionProducer;
import ru.kai.homework.transaction.mapper.TransactionMapper;
import ru.kai.homework.transaction.model.Transaction;
import ru.kai.homework.transaction.model.dto.request.TransactionRequest;
import ru.kai.homework.transaction.service.TransactionService;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionMapper mapper;
    private final TransactionProducer producer;

    @Override
    public Transaction createTransaction(TransactionRequest request) {
        Transaction transaction = mapper.toEntity(request);
        producer.sendMessage(transaction);
        return transaction;
    }
}
