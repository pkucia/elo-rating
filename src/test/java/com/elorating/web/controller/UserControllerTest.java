package com.elorating.web.controller;

import com.elorating.email.EmailsNotifications;
import com.elorating.league.LeagueDocument;
import com.elorating.player.PlayerDocument;
import com.elorating.player.PlayerService;
import com.elorating.user.UserDocument;
import com.elorating.user.UserService;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

public class UserControllerTest extends BaseControllerTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PlayerService playerService;

    @Before
    public void setUp() throws Exception {
        mockMvc = webAppContextSetup(webApplicationContext).build();
        this.league = leagueService.save(new LeagueDocument(null, "Test league"));
    }

    @After
    public void tearDown() throws Exception {
        userService.deleteAll();
        playerService.deleteAll();
        leagueService.deleteAll();
    }

    @Test
    public void testGet() throws Exception {
        UserDocument user = userService.save(new UserDocument("Test user", "test@mail.com"));
        String url = "/api/users/" + user.getId();
        mockMvc.perform(get(url)
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    @Ignore
    @Test
    public void testSignIn() throws Exception {
        // TODO mock GoogleIdTokenVerifier to return fake UserDocument
        String token = "example_token";
        mockMvc.perform(post("/users/sign-in")
                .contentType(contentType)
                .content(token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", Matchers.is("test@mail.com")));
    }

    @Test
    public void testAssignLeague() throws Exception {
        UserDocument user = new UserDocument("Test user");
        user.addLeague(league);
        userService.save(user);
        LeagueDocument leagueToAssign = leagueService.save(new LeagueDocument(null, "To assign"));
        String url = "/api/leagues/" + leagueToAssign.getId() + "/users/" + user.getId() + "/assign-league/";
        mockMvc.perform(post(url)
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.leagues", Matchers.hasSize(2)));
        LeagueDocument updatedLeague = leagueService.get(leagueToAssign.getId()).get();
        Assert.assertTrue(updatedLeague.getUsers().size() == 1);
    }

    @Test
    public void testInviteNewUser() throws Exception {
        UserDocument user = userService.save(new UserDocument("UserDocument who invite"));
        UserDocument userToInvite = new UserDocument("UserDocument to invite", "t.morek@gmail.com");
        userToInvite.addLeague(league);
        String url = "/api/leagues/" + league.getId() + "/users/" + user.getId() + "/invite";
        mockMvc.perform(post(url)
                .contentType(contentType)
                .header("Origin", "http://elo.com")
                .content(objectMapper.writeValueAsString(userToInvite)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email", is(userToInvite.getEmail())))
                .andExpect(jsonPath("$.googleId", isEmptyOrNullString()))
                .andExpect(jsonPath("$.invitationToken", isEmptyOrNullString()))
                .andExpect(jsonPath("$.leagues", Matchers.hasSize(1)));
    }

    @Test
    public void testInviteExistingUser() throws Exception {
        UserDocument user = userService.save(new UserDocument("UserDocument who invite"));
        UserDocument userToInvite = userService.save(new UserDocument("UserDocument to invite", "t.morek@gmail.com"));
        userToInvite.addLeague(league);
        String url = "/api/leagues/" + league.getId() + "/users/" + user.getId() + "/invite";
        mockMvc.perform(post(url)
                .contentType(contentType)
                .header("Origin", "http://elo.com")
                .content(objectMapper.writeValueAsString(userToInvite)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userToInvite.getId())))
                .andExpect(jsonPath("$.email", is(userToInvite.getEmail())))
                .andExpect(jsonPath("$.leagues[0].id", is(league.getId())));
        LeagueDocument updatedLeague = leagueService.get(league.getId()).get();
        Assert.assertEquals(updatedLeague.getUsers().size(), 1);
    }

    @Ignore // Test failing when is run with other tests. WTF?
    @Test
    public void testInviteNewUserWithPlayer() throws Exception {
        UserDocument user = userService.save(new UserDocument("UserDocument who invite"));
        UserDocument userToInvite = new UserDocument("UserDocument to invite", "t.morek@gmail.com");
        PlayerDocument player = playerService.save(new PlayerDocument("PlayerDocument to connect", league));
        userToInvite.addPlayer(player);
        userToInvite.addLeague(league);
        String url = "/api/leagues/" + league.getId() + "/users/" + user.getId() + "/invite";
        mockMvc.perform(post(url)
                .contentType(contentType)
                .header("Origin", "http://elo.com")
                .content(objectMapper.writeValueAsString(userToInvite)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email", is(userToInvite.getEmail())))
                .andExpect(jsonPath("$.googleId", isEmptyOrNullString()))
                .andExpect(jsonPath("$.invitationToken", isEmptyOrNullString()))
                .andExpect(jsonPath("$.leagues", Matchers.hasSize(1)))
                .andExpect(jsonPath("$.players[0].id", is(player.getId())));
    }

    @Ignore // Test failing when is run with other tests. WTF?
    @Test
    public void testInviteExistingUserWithPlayer() throws Exception {
        UserDocument user = userService.save(new UserDocument("UserDocument who invite"));
        UserDocument userToInvite = userService.save(new UserDocument("UserDocument to invite", "t.morek@gmail.com"));
        PlayerDocument player = playerService.save(new PlayerDocument("PlayerDocument to connect", league));
        userToInvite.addPlayer(player);
        userToInvite.addLeague(league);
        String url = "/api/leagues/" + league.getId() + "/users/" + user.getId() + "/invite";
        mockMvc.perform(post(url)
                .contentType(contentType)
                .header("Origin", "http://elo.com")
                .content(objectMapper.writeValueAsString(userToInvite)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userToInvite.getId())))
                .andExpect(jsonPath("$.email", is(userToInvite.getEmail())))
                .andExpect(jsonPath("$.leagues[0].id", is(league.getId())))
                .andExpect(jsonPath("$.players[0].id", is(player.getId())));
        LeagueDocument updatedLeague = leagueService.get(league.getId()).get();
        PlayerDocument updatedPlayer = playerService.get(player.getId()).get();
        Assert.assertEquals(updatedLeague.getUsers().size(), 1);
        Assert.assertEquals(updatedPlayer.getUser().getId(), userToInvite.getId());
    }

    @Test
    public void testFindByUsername() throws Exception {
        userService.save(new UserDocument("Name111"));
        userService.save(new UserDocument("name112"));
        userService.save(new UserDocument("name222"));
        String url = "/api/users/find-by-name" + "?name=name1";
        mockMvc.perform(get(url)
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)));
    }

    @Test
    public void testFindByUsernameEmptyResult() throws Exception {
        userService.save(new UserDocument("user123"));
        String url = "/api/users/find-by-name" + "?name=name";
        mockMvc.perform(get(url)
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(0)));
    }

    @Test
    public void testVerifySecurityToken() throws Exception {
        String token = UUID.randomUUID().toString();
        UserDocument user = new UserDocument();
        user.setInvitationToken(token);
        userService.save(user);
        String url = "/api/users/verify-security-token";
        mockMvc.perform(post(url)
                .contentType(contentType)
                .content(token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(true)));
        mockMvc.perform(post(url)
                .contentType(contentType)
                .content("fake_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(false)));
    }

    @Test
    public void testCreatePlayer() throws Exception {
        UserDocument user = userService.save(new UserDocument("Test user"));
        String url = "/api/leagues/" + league.getId() + "/users/" + user.getId() + "/create-player";
        mockMvc.perform(post(url)
                .contentType(contentType)
                .content(league.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.players[0].username", is(user.getName())))
                .andExpect(jsonPath("$.players[0].rating", is(1000)));
    }

    @Test
    public void test_userWithDefaultEmailNotificationsSetToFalse() throws Exception {
        String userName = "user";
        UserDocument user = userService.save(new UserDocument(userName));
        String url = "/api/users/find-by-name" + "?name=" + userName;
        mockMvc.perform(get(url)
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].emailsNotifications.scheduledMatchNotification", is(false)))
                .andExpect(jsonPath("$[0].emailsNotifications.editedMatchNotification", is(false)))
                .andExpect(jsonPath("$[0].emailsNotifications.cancelledMatchNotification", is(false)));
    }

    @Test
    public void test_sendEmailNotificationsToUpdate_success() throws Exception {
        String userName = "user";
        UserDocument user = userService.save(new UserDocument(userName));
        String findUserUrl = "/api/users/find-by-name" + "?name=" + userName;
        mockMvc.perform(get(findUserUrl)
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].emailsNotifications.scheduledMatchNotification", is(false)))
                .andExpect(jsonPath("$[0].emailsNotifications.editedMatchNotification", is(false)))
                .andExpect(jsonPath("$[0].emailsNotifications.cancelledMatchNotification", is(false)));

        boolean newScheduledNotification = true;
        boolean newEditedNotification = true;
        boolean newCancelledNotification = true;
        EmailsNotifications emailsNotifications = new EmailsNotifications(newScheduledNotification, newEditedNotification, newCancelledNotification);

        String updateNotificationsUrl = "/api/users/emails-notifications?user_id=" + user.getId();
        mockMvc.perform(post(updateNotificationsUrl)
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(emailsNotifications)))
                .andExpect(jsonPath("$.emailsNotifications.scheduledMatchNotification", is(newScheduledNotification)))
                .andExpect(jsonPath("$.emailsNotifications.editedMatchNotification", is(newEditedNotification)))
                .andExpect(jsonPath("$.emailsNotifications.cancelledMatchNotification", is(newCancelledNotification)));

    }

    @Test
    public void test_updateUserTimezone() throws Exception {
        String userName = "user";
        UserDocument user = userService.save(new UserDocument(userName));
        String findUserUrl = "/api/users/find-by-name" + "?name=" + userName;
        mockMvc.perform(get(findUserUrl)
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].timezone", isEmptyOrNullString()));

        String timezone = "GMT+1:00 Europe/Belgrade";
        String updateTimezoneUrl = "/api/users/timezone?user_id=" + user.getId();

        mockMvc.perform(post(updateTimezoneUrl)
                .contentType(contentType)
                .content(timezone))
                .andExpect(jsonPath("$.timezone", is(timezone)));
    }
}