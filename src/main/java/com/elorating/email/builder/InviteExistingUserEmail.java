package com.elorating.email.builder;

import com.elorating.email.EmailBuilder;
import com.elorating.league.LeagueDocument;
import org.thymeleaf.context.Context;

public class InviteExistingUserEmail extends EmailBuilder {

    private String currentUser;
    private LeagueDocument league;

    public InviteExistingUserEmail(String recipient, String currentUser,
                                   String originUrl, LeagueDocument league) {
        this.recipient = recipient;
        this.currentUser = currentUser;
        this.originUrl = originUrl;
        this.league = league;
    }

    @Override
    public void buildRecipient() {
        email.setRecipient(recipient);
    }

    @Override
    public void buildSubject() {
        email.setSubject("EloRating invitation");
    }

    @Override
    public void buildTemplateName() {
        email.setTemplateName("inviteExisting");
    }

    @Override
    public void buildContext() {
        Context context = email.getContext();
        String redirectUrl = originUrl + "/leagues/" + league.getId();
        context.setVariable("redirectUrl", redirectUrl);
        context.setVariable("leagueName", league.getName());
        context.setVariable("username", currentUser);
    }
}
