package ru.kai.homework.transaction.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.kai.homework.transaction.mapper.TransactionMapper;
import ru.kai.homework.transaction.model.Transaction;
import ru.kai.homework.transaction.model.dto.TransactionDto;

import java.util.concurrent.ExecutionException;

@Slf4j
@RequiredArgsConstructor
@Component
public class TransactionProducer {
    private final KafkaTemplate<String, TransactionDto> template;
    private final TransactionMapper mapper;

    public void sendMessage(Transaction transaction) {
        try {
            template.sendDefault(mapper.toDto(transaction)).get();
            template.flush();
        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to send message: {}", e.getMessage(), e);
        }
    }
}
