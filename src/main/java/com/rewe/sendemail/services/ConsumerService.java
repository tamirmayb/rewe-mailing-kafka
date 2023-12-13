package com.rewe.sendemail.services;

import com.rewe.sendemail.models.Email;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Arrays;

@Service
public class ConsumerService {
    private static final Logger log = LogManager.getLogger(ConsumerService.class);
    private final EmailSenderService emailSenderService;

    @Autowired
    public ConsumerService(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    @KafkaListener(topicPartitions
            = @TopicPartition(topic = "${kafka.topics.data}", partitions = { "0" }))
    public void handleEmailFromGoogle(Email email) throws MessagingException, IOException {
        log.info(String.format("Got an email from gmail() -> %s", email.getFrom()));
        sendConfirmationEmails(email);
    }

    @KafkaListener(topicPartitions
            = @TopicPartition(topic = "${kafka.topics.data}", partitions = { "1" }))
    public void handleEmailFromYahoo(Email email) throws MessagingException, IOException {
        log.info(String.format("Got an email from yahoo() -> %s", email.getFrom()));
        sendConfirmationEmails(email);
    }

    @KafkaListener(topicPartitions
            = @TopicPartition(topic = "${kafka.topics.data}", partitions = { "2" }))
    public void handleEmailFromOther(Email email) throws MessagingException, IOException {
        log.info(String.format("Got an email from other source() -> %s", email.getFrom()));
        sendConfirmationEmails(email);
    }

    private void sendConfirmationEmails(Email email) throws MessagingException, IOException {
        log.info(String.format("sendConfirmationEmail() -> to: %s", email.getFrom()));
        var subject = email.getSubject();
        var to = email.getFrom();

        Email confimationEmail = Email.builder()
                .withTo(Arrays.stream(to.split(",")).toList())
                .withFrom("From REWE <support@rewe.de>")
                .withContent("---")
                .withSubject(subject)
                .build();
        emailSenderService.sendConfirmationEmail(confimationEmail);
        log.info(" Email sent successfully");
    }
}
