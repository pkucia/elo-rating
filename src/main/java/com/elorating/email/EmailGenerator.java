package com.elorating.email;


import com.elorating.match.MatchDocument;

import java.util.Set;

public interface EmailGenerator {

    public final String SCHEDULE_MATCH = "SCHEDULE";
    public final String CANCEL_MATCH = "CANCEL";
    public final String EDIT_MATCH = "EDIT";

    public Set<EmailBuilder> generateEmails(MatchDocument match, String emailType, String originUrl);
}
