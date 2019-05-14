package com.elorating.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Iterator;
import java.util.Set;

@Service
class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Value("${spring.mail.username}")
    private String sender;

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Autowired
    public EmailServiceImpl(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public boolean send(Email email) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, false);
            String content = templateEngine.process(email.getTemplateName(), email.getContext());
            messageHelper.setFrom(sender);
            messageHelper.setTo(email.getRecipient());
            messageHelper.setSubject(email.getSubject());
            messageHelper.setText(content, true);
            javaMailSender.send(message);
            return true;
        } catch (MessagingException | MailAuthenticationException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    @Override
    public void sendEmails(Set emails) {
        try {
            Iterator iterator = emails.iterator();
            while(iterator.hasNext()) {
                EmailBuilder emailBuilder = (EmailBuilder) iterator.next();
                boolean emailSent = sendEmail(emailBuilder);
                if (emailSent) {
                    sendEmailLog(emailBuilder);
                }
            }
        } catch (Exception e) {
            logger.error("Error while sending email");
            e.printStackTrace();
        }
    }

    @Override
    public boolean sendEmail(EmailBuilder emailBuilder) {
        EmailDirector emailDirector = new EmailDirector();
        emailDirector.setBuilder(emailBuilder);
        return this.send(emailDirector.build());
    }

    private void sendEmailLog(EmailBuilder emailBuilder) {
        StringBuilder stringBuilder = new StringBuilder().append("Email: " + emailBuilder.getEmail().getSubject());
        stringBuilder.append(", sent to: " + emailBuilder.getEmail().getRecipient());
        logger.info(stringBuilder.toString());
    }
}
