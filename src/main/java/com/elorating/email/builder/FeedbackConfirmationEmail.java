package com.elorating.email.builder;

import com.elorating.email.EmailBuilder;
import org.thymeleaf.context.Context;

public class FeedbackConfirmationEmail extends EmailBuilder {

    private String text;

    public FeedbackConfirmationEmail(String recipient, String text) {
        this.recipient = recipient;
        this.text = text;
    }

    @Override
    public void buildRecipient() {
        email.setRecipient(recipient);
    }

    @Override
    public void buildSubject() {
        email.setSubject("EloRating feedback confirmation");
    }

    @Override
    public void buildTemplateName() {
        email.setTemplateName("feedbackConfirmation");
    }

    @Override
    public void buildContext() {
        Context context = email.getContext();
        context.setVariable("text", text);
        email.setContext(context);
    }
}
