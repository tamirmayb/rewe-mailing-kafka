package com.rewe.sendemail.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class KafkaTopicConfig {

    @Value("${kafka.servers}")
    private String kafkaServers;

    @Value("${kafka.topics.data}")
    private String topicName;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic emailTopic() {
        log.info("trying to create topic with name " + topicName);
        NewTopic newTopic = new NewTopic(topicName, 3, (short) 1);
        log.info("Topic with name " + topicName + "created");
        return newTopic;
    }
}