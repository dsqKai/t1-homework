package ru.kai.homework.correction.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.kai.homework.correction.model.Correction;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Slf4j
@RequiredArgsConstructor
@Component
public class CorrectionProducer {
    private final KafkaTemplate<String, UUID> template;

    public void sendMessage(Correction correction) {
        try {
            template.sendDefault(correction.getTransactionId()).get();
            template.flush();
        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to send message: {}", e.getMessage(), e);
        }
    }
}
