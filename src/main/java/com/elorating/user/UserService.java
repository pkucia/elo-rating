package com.elorating.user;

import com.elorating.common.CrudService;
import com.elorating.player.PlayerDocument;

import java.util.List;
import java.util.TimeZone;

public interface UserService extends CrudService<UserDocument> {

    UserDocument findByEmail(String email);
    UserDocument findByInvitationToken(String token);
    List<UserDocument> findByNameLikeIgnoreCase(String name);
    UserDocument connectUserToLeagueAndPlayer(UserDocument user);
    UserDocument connectUserToLeague(UserDocument user);
    UserDocument connectUserToPlayer(UserDocument user);
    UserDocument checkForPendingInvitation(UserDocument userFromGoogle);
    UserDocument saveOrUpdateUser(UserDocument userFromGoogle);
    UserDocument saveOrUpdateUser(UserDocument userFromGoogle, TimeZone timeZone);
    UserDocument inviteNewUser(String currentUser, UserDocument userToInvite, String originUrl);
    UserDocument inviteExistingUser(String currentUser, UserDocument requestUser, String originUrl);
    UserDocument connectUserAndLeague(String userId, String leagueId);
    UserDocument connectUserAndPlayer(String userId, String playerId);
    PlayerDocument createPlayerForUser(String userId, String leagueId);
}