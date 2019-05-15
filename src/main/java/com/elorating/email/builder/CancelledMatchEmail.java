package com.elorating.email.builder;

import com.elorating.email.EmailBuilder;
import com.elorating.email.EmailStrings;
import com.elorating.league.LeagueDocument;
import org.thymeleaf.context.Context;

public class CancelledMatchEmail extends EmailBuilder {

    private String opponent;
    private LeagueDocument league;

    public CancelledMatchEmail(String opponent, String recipient, String originUrl, LeagueDocument league) {
        this.opponent = opponent;
        this.recipient = recipient;
        this.originUrl = originUrl;
        this.league = league;
    }

    @Override
    public void buildRecipient() {
        email.setRecipient(recipient);
    }

    @Override
    public void buildSubject() {
        email.setSubject(EmailStrings.CANCELLED_MATCH);
    }

    @Override
    public void buildTemplateName() {
        email.setTemplateName(EmailStrings.CANCELLED_MATCH_TEMPLATE);
    }

    @Override
    public void buildContext() {
        Context context = email.getContext();
        String redirectUrl = originUrl + "/leagues/" + this.league.getId() + "/matches";
        context.setVariable("redirectUrl", redirectUrl);
        context.setVariable("opponent", opponent);
    }
}
