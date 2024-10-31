package ru.kai.homework.client.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.kai.homework.client.model.Transaction;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Slf4j
@RequiredArgsConstructor
@Component
public class TransactionProducer {
    private final KafkaTemplate<String, UUID> template;

    public void sendMessage(Transaction transaction) {
        try {
            template.sendDefault(transaction.getId()).get();
            template.flush();
        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to send message: {}", e.getMessage(), e);
        }
    }
}
