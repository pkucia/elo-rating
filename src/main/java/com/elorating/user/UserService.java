package com.elorating.user;

import com.elorating.model.User;
import com.elorating.player.Player;

import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

public interface UserService {
    Optional<User> get(String id);
    List<User> getAll();
    User save(User user);
    List<User> save(Iterable<User> users);
    void delete(String id);
    void deleteAll();
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