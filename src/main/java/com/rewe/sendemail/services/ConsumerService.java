package com.rewe.sendemail.services;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rewe.sendemail.models.Email;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
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

    @KafkaListener(topics = "${kafka.topics.data}")
    public void sendConfirmationEmails(ConsumerRecord<?, ?> commandsRecord) throws MessagingException, IOException {
        log.info(String.format("sendConfirmationEmails() -> Topic: %s", commandsRecord.topic()));
        JsonElement object = new Gson().fromJson(commandsRecord.value().toString(), JsonObject.class);
        var subject = object.getAsJsonObject().get("subject").getAsString();
        var to = object.getAsJsonObject().get("email").getAsString();

        Email email = Email.builder()
                .withTo(Arrays.stream(to.split(",")).toList())
                .withFrom("From REWE <support@rewe.de>")
                .withContent("---")
                .withSubject(subject)
                .build();
        emailSenderService.sendConfirmationEmail(email);
        log.info(" Email sent successfully");
    }
}
