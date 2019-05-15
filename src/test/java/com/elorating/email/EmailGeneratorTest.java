package com.elorating.email;

import com.elorating.email.builder.CancelledMatchEmail;
import com.elorating.email.builder.EditMatchEmail;
import com.elorating.email.builder.ScheduledMatchEmail;
import com.elorating.league.LeagueDocument;
import com.elorating.match.MatchDocument;
import com.elorating.player.PlayerDocument;
import com.elorating.user.UserDocument;
import com.elorating.web.utils.MatchTestUtils;
import com.elorating.web.utils.PlayerTestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

@Ignore
public class EmailGeneratorTest {

    @Autowired
    EmailGenerator emailGenerator;

    LeagueDocument league;
    List<PlayerDocument> players = PlayerTestUtils.generatePlayerList(2, league);
    MatchDocument match = MatchTestUtils.generateMatch(league, players.get(0), players.get(1), false);
    String originUrl = "originUrl";
    UserDocument user = new UserDocument("testUser", "test@email.com");
    String timezone = "GMT+1:00 Europe/Warsaw";

    @Before
    public void setup() {
        user.setTimezone(timezone);
        league = new LeagueDocument("test");
        players = PlayerTestUtils.generatePlayerList(2, league);
        for (int i = 0; i < players.size(); i++) {
            PlayerDocument player = players.get(i);
            player.setUser(user);
            players.set(i, player);
        }
        match = MatchTestUtils.generateMatch(league, players.get(0), players.get(1), false);
    }

    private EmailsNotifications setEmailNotifications(boolean scheduledNotifications, boolean editedNotifications, boolean canceledNotifications) {
        EmailsNotifications emailsNotifications = new EmailsNotifications(scheduledNotifications, editedNotifications, canceledNotifications);
        return emailsNotifications;
    }

    @Test
    public void test_generateScheduleMatches_shouldReturnTwoEmails_success() throws Exception {
        user.setEmailNotifications(setEmailNotifications(true, false, false));
        Set<EmailBuilder> scheduleMatchEmails = emailGenerator.generateEmails(match, emailGenerator.SCHEDULE_MATCH, originUrl);

        Assert.assertNotNull(scheduleMatchEmails);
        Assert.assertTrue(scheduleMatchEmails.size() == 2);
        for (EmailBuilder email : scheduleMatchEmails) {
            Assert.assertTrue(email.getClass() == ScheduledMatchEmail.class);
        }
    }

    @Test
    public void test_generateEditMatches_shouldReturnTwoEmails_success() throws Exception {
        user.setEmailNotifications(setEmailNotifications(false, true, false));
        Set<EmailBuilder> editMatchEmails = emailGenerator.generateEmails(match, emailGenerator.EDIT_MATCH, originUrl);

        Assert.assertNotNull(editMatchEmails);
        Assert.assertTrue(editMatchEmails.size() == 2);
        for (EmailBuilder email : editMatchEmails) {
            Assert.assertTrue(email.getClass() == EditMatchEmail.class);
        }
    }

    @Test
    public void test_generateCancelMatches_shouldReturnTwoEmails_success() throws Exception {
        user.setEmailNotifications(setEmailNotifications(false, false, true));
        Set<EmailBuilder> cancelMatchEmails = emailGenerator.generateEmails(match, emailGenerator.CANCEL_MATCH, originUrl);

        Assert.assertNotNull(cancelMatchEmails);
        Assert.assertTrue(cancelMatchEmails.size() == 2);
        for (EmailBuilder email : cancelMatchEmails) {
            Assert.assertTrue(email.getClass() == CancelledMatchEmail.class);
        }
    }

    @Test
    public void test_emailNotifications_allEmailsSetToFalse_shouldNotGenerateEmails() throws Exception {
        user.setEmailNotifications(new EmailsNotifications(false, false, false));
        Set<EmailBuilder> scheduleMatchEmails = emailGenerator.generateEmails(match, emailGenerator.SCHEDULE_MATCH, originUrl);
        Set<EmailBuilder> editMatchEmails = emailGenerator.generateEmails(match, emailGenerator.EDIT_MATCH, originUrl);
        Set<EmailBuilder> cancelMatchEmails = emailGenerator.generateEmails(match, emailGenerator.CANCEL_MATCH, originUrl);

        Assert.assertTrue(scheduleMatchEmails.size() == 0);
        Assert.assertTrue(editMatchEmails.size() == 0);
        Assert.assertTrue(cancelMatchEmails.size() == 0);
    }
}
