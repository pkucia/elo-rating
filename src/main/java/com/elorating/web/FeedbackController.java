package com.elorating.web;

import com.elorating.model.Feedback;
import com.elorating.email.EmailService;
import com.elorating.email.EmailBuilder;
import com.elorating.email.EmailDirector;
import com.elorating.email.builder.FeedbackConfirmationEmail;
import com.elorating.email.builder.FeedbackEmail;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Api(value = "Feedback", description = "Feedback API")
public class FeedbackController {

    private static final Logger logger = LoggerFactory.getLogger(FeedbackController.class);

    @Value("${spring.mail.feedback.address}")
    private String feedbackAddress;

    @Autowired
    private EmailService emailService;

    @CrossOrigin
    @RequestMapping(value = "/feedback/send", method = RequestMethod.POST)
    @ApiOperation(value = "Send feedback", notes = "Send feedback email")
    public ResponseEntity<Boolean> send(@RequestBody Feedback feedback) {
        boolean success = false;
        EmailBuilder emailBuilder = new FeedbackEmail(feedback.getSender(), feedbackAddress, feedback.getText());
        if (sendEmail(emailBuilder)) {
            emailBuilder = new FeedbackConfirmationEmail(feedback.getSender(), feedback.getText());
            success = sendEmail(emailBuilder);
        }
        return new ResponseEntity<>(success, HttpStatus.OK);
    }

    private boolean sendEmail(EmailBuilder emailBuilder) {
        EmailDirector emailDirector = new EmailDirector();
        emailDirector.setBuilder(emailBuilder);
        return emailService.send(emailDirector.build());
    }
}
