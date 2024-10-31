package ru.kai.homework.client.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;
import ru.kai.homework.client.model.dto.request.TransactionRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Configuration
public class KafkaConsumerConfig {

    @Value("${t1.kafka.bootstrap-servers}")
    private String servers;
    @Value("${t1.kafka.consumer.group-id}")
    private String groupId;

    private Map<String, Object> consumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }

    @Bean
    public ConsumerFactory<String, UUID> correctionConsumerFactory() {
        Map<String, Object> props = consumerProps();
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, UUID.class.getName());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "ru.kai.homework.client.model.dto");
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(UUID.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UUID> correctionKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UUID> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factoryBuilder(correctionConsumerFactory(), factory);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, TransactionRequest> transactionConsumerFactory() {
        Map<String, Object> props = consumerProps();
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "ru.kai.homework.client.model.dto.request.TransactionRequest");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "ru.kai.homework.client.model.dto");
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(TransactionRequest.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TransactionRequest> transactionKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TransactionRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factoryBuilder(transactionConsumerFactory(), factory);
        return factory;
    }

    private <T> void factoryBuilder(ConsumerFactory<String, T> consumerFactory, ConcurrentKafkaListenerContainerFactory<String, T> factory) {
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        factory.setConcurrency(1);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
        factory.getContainerProperties().setPollTimeout(5000);
        factory.setCommonErrorHandler(errorHandler());
    }

    private CommonErrorHandler errorHandler() {
        DefaultErrorHandler handler = new DefaultErrorHandler(new FixedBackOff(1000, 3));
        handler.addNotRetryableExceptions(IllegalStateException.class);
        handler.setRetryListeners((record, ex, deliveryAttempt) -> {
            log.error("RetryListener: message = {}, offset = {}, attempt = {}", ex.getMessage(), record.offset(), deliveryAttempt);
        });
        return handler;
    }
}
