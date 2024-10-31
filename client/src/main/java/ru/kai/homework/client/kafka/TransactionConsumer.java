package ru.kai.homework.client.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.kai.homework.client.model.dto.request.TransactionRequest;
import ru.kai.homework.client.service.TransactionService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class TransactionConsumer {
    private final TransactionService transactionService;

    @KafkaListener(id = "${t1.kafka.listener.transaction-process}",
            topics = "${t1.kafka.topic.transaction-process}",
            containerFactory = "transactionKafkaListenerContainerFactory")
    public void listenerRegisterTransaction(@Payload List<TransactionRequest> messageList) {
        for(TransactionRequest transactionRequest : messageList) {
            try {
                transactionService.handleTransaction(transactionRequest);
            } catch (Exception e) {
                log.error("Failed to process Kafka message transaction ID: {}", transactionRequest.getId(), e);
            }
        }

    }

    @KafkaListener(id = "${t1.kafka.listener.transaction-correction}",
            topics = "${t1.kafka.topic.correction-transaction}",
            containerFactory = "correctionKafkaListenerContainerFactory")
    public void listenerCorrectionTransaction(@Payload List<UUID> messageList) {
        for(UUID transactionId : messageList) {
            try {
                transactionService.retryTransactionWithErrorHandling(transactionId);
            } catch (Exception e) {
                log.error("Failed to process Kafka message transaction ID: {}", transactionId, e);
            }
        }

    }


}
