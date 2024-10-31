package ru.kai.homework.correction.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.kai.homework.correction.service.CorrectionService;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class CorrectionConsumer {
    private final CorrectionService correctionService;

    @KafkaListener(id = "${t1.kafka.listener.transaction-error}",
            topics = "${t1.kafka.topic.error-transaction}",
            containerFactory = "correctionKafkaListenerContainerFactory")
    public void listener(@Payload UUID transactionId) {
        try {
            correctionService.newCorrection(transactionId);
        } catch (Exception e) {
            log.error("Failed to process Kafka message transaction ID: {}", transactionId, e);
        }
    }
}
