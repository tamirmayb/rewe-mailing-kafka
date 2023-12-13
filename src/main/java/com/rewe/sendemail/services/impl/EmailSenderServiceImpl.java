package com.rewe.sendemail.services.impl;

import com.rewe.sendemail.models.Email;
import com.rewe.sendemail.services.EmailSenderService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Service
public class EmailSenderServiceImpl implements EmailSenderService {

    private static final Logger log = LogManager.getLogger(EmailSenderServiceImpl.class);

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    public EmailSenderServiceImpl(JavaMailSender javaMailSender, SpringTemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendEmail(Email email) {
        String[] array = new String[email.getTo().size()];

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email.getTo().toArray(array));
        mailMessage.setFrom(email.getFrom());
        mailMessage.setSubject(email.getSubject());
        mailMessage.setText(email.getContent());
        javaMailSender.send(mailMessage);

    }

    @Override
    public void sendConfirmationEmail(Email email) throws MessagingException {
        log.info(String.format("sendConfirmationEmail (%s)", email.getTo()));
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        String[] array = new String[email.getTo().size()];
        
        Context context = new Context();
        String html = templateEngine.process("confirmation", context);
        helper.setTo(email.getTo().toArray(array));
        helper.setText(html, true);
        helper.setSubject(email.getSubject());
        helper.setFrom(email.getFrom());
        javaMailSender.send(message);
    }
}
