package com.elorating.email;

import java.util.Set;

public interface EmailService {

    boolean send(Email email);
    void sendEmails(Set emails);
    boolean sendEmail(EmailBuilder emailBuilder);
}
