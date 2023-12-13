package com.rewe.sendemail.services;

import com.rewe.sendemail.models.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProducerService {

    @Value("${kafka.topics.data}")
    private String topicName;

    @Autowired
    private KafkaTemplate<String, Email> kafkaTemplate;

    public void sendMessage(Email msg) {
        kafkaTemplate.send(topicName, msg);
    }

}
