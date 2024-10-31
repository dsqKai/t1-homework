package ru.kai.homework.transaction.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.kai.homework.transaction.kafka.TransactionProducer;
import ru.kai.homework.transaction.mapper.TransactionMapper;
import ru.kai.homework.transaction.model.dto.TransactionDto;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig<T> {
    @Value("${t1.kafka.bootstrap-server}")
    private String bootstrapServers;

    @Value("${t1.kafka.topic.transaction}")
    private String transactionTopic;

    @Bean
    public KafkaTemplate<String, T> kafkaTemplate(ProducerFactory<String, T> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    @Primary
    public TransactionProducer producerTransaction(KafkaTemplate<String, TransactionDto> template, TransactionMapper mapper) {
        template.setDefaultTopic(transactionTopic);
        return new TransactionProducer(template, mapper);
    }

    @Bean("producerFactory")
    public ProducerFactory<String, T> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, false);
        return new DefaultKafkaProducerFactory<>(props);
    }
}
