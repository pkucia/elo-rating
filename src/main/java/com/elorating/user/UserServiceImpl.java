package com.elorating.user;

import com.elorating.common.AbstractCrudService;
import com.elorating.league.LeagueDocument;
import com.elorating.league.LeagueRepository;
import com.elorating.player.PlayerDocument;
import com.elorating.player.PlayerRepository;
import com.elorating.email.EmailService;
import com.elorating.email.Email;
import com.elorating.email.EmailBuilder;
import com.elorating.email.EmailDirector;
import com.elorating.email.builder.InviteExistingUserEmail;
import com.elorating.email.builder.InviteNewUserEmail;
import com.elorating.web.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;

@Service
class UserServiceImpl extends AbstractCrudService<UserDocument, UserRepository> implements UserService {

    private final LeagueRepository leagueRepository;
    private final PlayerRepository playerRepository;
    private final EmailService emailService;

    @Autowired
    public UserServiceImpl(LeagueRepository leagueRepository, UserRepository userRepository,
                           PlayerRepository playerRepository, EmailService emailService) {
        super(userRepository);
        this.leagueRepository = leagueRepository;
        this.playerRepository = playerRepository;
        this.emailService = emailService;
    }

    @Override
    public UserDocument findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public UserDocument findByInvitationToken(String token) {
        return repository.findByInvitationToken(token);
    }

    @Override
    public List<UserDocument> findByNameLikeIgnoreCase(String name) {
        return repository.findByNameLikeIgnoreCase(name);
    }

    @Override
    public UserDocument connectUserToLeagueAndPlayer(UserDocument user) {
        connectUserToLeague(user);
        if (user.getPlayers() != null && user.getPlayers().size() > 0)
            connectUserToPlayer(user);
        return user;
    }

    @Override
    public UserDocument connectUserToLeague(UserDocument user) {
        String leagueId = user.getLeagues().get(0).getId();
        leagueRepository.findById(leagueId).ifPresent(league -> {
            league.getUsers().add(user);
            leagueRepository.save(league);
        });
        return user;
    }

    @Override
    public UserDocument connectUserToPlayer(UserDocument user) {
        String playerId = user.getPlayers().get(0).getId();
        playerRepository.findById(playerId).ifPresent(player -> {
            player.setUser(user);
            playerRepository.save(player);
        });
        return user;
    }

    @Override
    public UserDocument checkForPendingInvitation(UserDocument userFromGoogle) {
        UserDocument user = repository.findByEmailAndInvitationTokenExists(userFromGoogle.getEmail());
        if (user != null) {
            user.clearInvitationToken();
            user.update(userFromGoogle);
            user.setGoogleId(userFromGoogle.getGoogleId());
            repository.save(user);
            user = connectUserToLeagueAndPlayer(user);
        }
        return user;
    }

    @Override
    public UserDocument saveOrUpdateUser(UserDocument userFromGoogle) {
        UserDocument savedUser = repository.findByGoogleId(userFromGoogle.getGoogleId());
        if (savedUser != null) {
            savedUser.update(userFromGoogle);
            savedUser = repository.save(savedUser);
        } else {
            savedUser = repository.save(userFromGoogle);
        }
        return savedUser;
    }

    @Override
    public UserDocument saveOrUpdateUser(UserDocument userFromGoogle, TimeZone timeZone) {
        UserDocument savedUser = repository.findByGoogleId(userFromGoogle.getGoogleId());
        if (savedUser != null) {
            savedUser.update(userFromGoogle);
            savedUser = setUserTimezone(savedUser, timeZone);
            savedUser = repository.save(savedUser);
        } else {
            userFromGoogle = setUserTimezone(userFromGoogle, timeZone);
            savedUser = repository.save(userFromGoogle);
        }
        return savedUser;
    }

    @Override
    public UserDocument inviteNewUser(String currentUser, UserDocument userToInvite, String originUrl) {
        String token = UUID.randomUUID().toString();
        userToInvite.setInvitationToken(token);
        repository.save(userToInvite);
        EmailBuilder emailBuilder = new InviteNewUserEmail(userToInvite.getEmail(), currentUser, originUrl, token);
        sendEmail(emailBuilder);
        userToInvite.clearInvitationToken();
        return userToInvite;
    }

    @Override
    public UserDocument inviteExistingUser(String currentUser, UserDocument requestUser, String originUrl) {
        LeagueDocument league = requestUser.getLeagues().get(0);
        UserDocument userFromDB = repository.findByEmail(requestUser.getEmail());
        UserDocument invitedUser = connectUserAndLeague(userFromDB.getId(), league.getId());
        if (requestUser.getPlayers() != null && requestUser.getPlayers().size() > 0)
            invitedUser = connectUserAndPlayer(userFromDB.getId(), requestUser.getPlayers().get(0).getId());
        EmailBuilder emailBuilder = new InviteExistingUserEmail(invitedUser.getEmail(), currentUser, originUrl, league);
        sendEmail(emailBuilder);
        return invitedUser;
    }

    @Override
    public UserDocument connectUserAndLeague(String userId, String leagueId) {
        Optional<UserDocument> user = repository.findById(userId);
        Optional<LeagueDocument> league = leagueRepository.findById(leagueId);
        if (user.isPresent() && league.isPresent()) {
            user.get().addLeague(league.get());
            repository.save(user.get());
            league.get().addUser(user.get());
            leagueRepository.save(league.get());
        }
        return user.orElse(null);
    }

    @Override
    public UserDocument connectUserAndPlayer(String userId, String playerId) {
        Optional<UserDocument> user = repository.findById(userId);
        Optional<PlayerDocument> player = playerRepository.findById(playerId);
        if (user.isPresent() && player.isPresent()) {
            user.get().addPlayer(player.get());
            repository.save(user.get());
            player.get().setUser(user.get());
            playerRepository.save(player.get());
        }
        return user.orElse(null);
    }

    @Override
    public PlayerDocument createPlayerForUser(String userId, String leagueId) {
        return repository.findById(userId).map(currentUser -> {
            LeagueDocument league = new LeagueDocument(leagueId);
            PlayerDocument player = new PlayerDocument(currentUser.getName(), league);
            playerRepository.save(player);
            return player;
        }).orElse(null);
    }

    private void sendEmail(EmailBuilder emailBuilder) {
        EmailDirector emailDirector = new EmailDirector();
        emailDirector.setBuilder(emailBuilder);
        Email email = emailDirector.build();
        emailService.send(email);
    }

    private UserDocument setUserTimezone(UserDocument user, TimeZone timeZone) {
        if (user.getTimezone() == null) {
            user.setTimezone(DateUtils.getTimezoneOffset(timeZone));
        }
        return user;
    }
}
