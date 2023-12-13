package com.rewe.sendemail.config;

import com.rewe.sendemail.models.Email;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {
    private static final Logger log = Logger.getLogger(KafkaConsumerConfig.class.getName());

    @Value("${kafka.servers}")
    private String kafkaServers;

    @Value("${kafka.group-id}")
    private String groupId;

    @Value("${kafka.auto-offset}")
    private String autoOffset;

    @Value("${kafka.mechanism}")
    private String mechanism;

    @Value("${kafka.username}")
    private String username;

    @Value("${kafka.password}")
    private String password;

    @Value("${kafka.protocol}")
    private String protocol;

    @Bean
    public ConsumerFactory<String, Email> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffset);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        //props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, protocol);
//        props.put(SaslConfigs.SASL_MECHANISM, mechanism);
//        props.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username='"
//                + KAFKA_USER + "' password='" + KAFKA_PASSWORD + "';");
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Email> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Email> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setErrorHandler(((thrownException, data) -> log.info("Kafka -> Exception in consumerConfig is: " + thrownException.getMessage() + " data: " + data)));
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(3000);
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(5);
        retryTemplate.setRetryPolicy(retryPolicy);
        return retryTemplate;
    }

}
