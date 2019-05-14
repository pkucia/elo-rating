package com.elorating.user;

import com.elorating.common.CrudService;
import com.elorating.player.Player;

import java.util.List;
import java.util.TimeZone;

public interface UserService extends CrudService<User> {

    User findByEmail(String email);
    User findByInvitationToken(String token);
    List<User> findByNameLikeIgnoreCase(String name);
    User connectUserToLeagueAndPlayer(User user);
    User connectUserToLeague(User user);
    User connectUserToPlayer(User user);
    User checkForPendingInvitation(User userFromGoogle);
    User saveOrUpdateUser(User userFromGoogle);
    User saveOrUpdateUser(User userFromGoogle, TimeZone timeZone);
    User inviteNewUser(String currentUser, User userToInvite, String originUrl);
    User inviteExistingUser(String currentUser, User requestUser, String originUrl);
    User connectUserAndLeague(String userId, String leagueId);
    User connectUserAndPlayer(String userId, String playerId);
    Player createPlayerForUser(String userId, String leagueId);
}