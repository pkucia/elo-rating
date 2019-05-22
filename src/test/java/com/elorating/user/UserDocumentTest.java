package com.elorating.user;

import com.elorating.league.LeagueDocument;
import com.elorating.player.PlayerDocument;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class UserDocumentTest {

    private UserDocument objectUnderTests;

    @Before
    public void setUp() {
        objectUnderTests = new UserDocument();
    }

    @Test
    public void shouldHaveDefaultFieldValuesAfterObjectCreation() {
        UserDocument.EmailsNotifications notifications = objectUnderTests.getEmailsNotifications();
        assertFalse(notifications.isCancelledMatchNotification());
        assertFalse(notifications.isEditedMatchNotification());
        assertFalse(notifications.isScheduledMatchNotification());
        assertNotNull(objectUnderTests.getLeagues());
        assertNotNull(objectUnderTests.getPlayers());
    }

    @Test
    public void shouldAddLeagueToList() {
    }

    @Test
    public void shouldAddPlayerToList() {
    }

    @Test
    public void shouldUpdate() {
        objectUnderTests.setGoogleId("googleId");
        objectUnderTests.setInvitationToken("token");
        UserDocument user = new UserDocument();
        user.setName("UserName");
        user.setGivenName("GivenName");
        user.setFamilyName("FamilyName");
        user.setEmail("user@mail.com");
        user.setPictureUrl("pictureUrl");
        user.setLastSignIn(Date.from(Instant.now().plusSeconds(100)));
        user.getEmailsNotifications().setCancelledMatchNotification(true);
        user.getEmailsNotifications().setEditedMatchNotification(true);
        user.getEmailsNotifications().setScheduledMatchNotification(true);
        user.setTimezone("timezone");
        user.addPlayer(new PlayerDocument());
        user.addLeague(new LeagueDocument());

        objectUnderTests.update(user);

        assertNotEquals(user.getGoogleId(), objectUnderTests.getGoogleId());
        assertNotEquals(user.getInvitationToken(), objectUnderTests.getInvitationToken());
        assertNotEquals(user.getLastSignIn(), objectUnderTests.getLastSignIn());
        assertEquals(0, objectUnderTests.getPlayers().size());
        assertEquals(0, objectUnderTests.getLeagues().size());
        assertEquals(user.getName(), objectUnderTests.getName());
        assertEquals(user.getGivenName(), objectUnderTests.getGivenName());
        assertEquals(user.getFamilyName(), objectUnderTests.getFamilyName());
        assertEquals(user.getEmail(), objectUnderTests.getEmail());
        assertEquals(user.getPictureUrl(), objectUnderTests.getPictureUrl());
        assertEquals(user.getEmailsNotifications().isCancelledMatchNotification(),
                objectUnderTests.getEmailsNotifications().isCancelledMatchNotification());
        assertEquals(user.getEmailsNotifications().isEditedMatchNotification(),
                objectUnderTests.getEmailsNotifications().isEditedMatchNotification());
        assertEquals(user.getEmailsNotifications().isScheduledMatchNotification(),
                objectUnderTests.getEmailsNotifications().isScheduledMatchNotification());
        assertEquals(user.getTimezone(), objectUnderTests.getTimezone());

    }
}