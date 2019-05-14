package com.elorating.user;

import com.elorating.league.League;
import com.elorating.league.LeagueRepository;
import com.elorating.model.User;
import com.elorating.player.Player;
import com.elorating.player.PlayerRepository;
import com.elorating.service.EmailService;
import com.elorating.service.email.Email;
import com.elorating.service.email.EmailBuilder;
import com.elorating.service.email.EmailDirector;
import com.elorating.service.email.InviteExistingUserEmail;
import com.elorating.service.email.InviteNewUserEmail;
import com.elorating.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;

@Service
class UserServiceImpl implements UserService {

    private final LeagueRepository leagueRepository;
    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;
    private final EmailService emailService;

    @Autowired
    public UserServiceImpl(LeagueRepository leagueRepository, UserRepository userRepository,
                           PlayerRepository playerRepository, EmailService emailService) {
        this.leagueRepository = leagueRepository;
        this.userRepository = userRepository;
        this.playerRepository = playerRepository;
        this.emailService = emailService;
    }

    @Override
    public Optional<User> get(String id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> save(Iterable<User> users) {
        return userRepository.saveAll(users);
    }

    @Override
    public void delete(String id) {
        userRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        userRepository.deleteAll();
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findByInvitationToken(String token) {
        return userRepository.findByInvitationToken(token);
    }

    @Override
    public List<User> findByNameLikeIgnoreCase(String name) {
        return userRepository.findByNameLikeIgnoreCase(name);
    }

    @Override
    public User connectUserToLeagueAndPlayer(User user) {
        connectUserToLeague(user);
        if (user.getPlayers() != null && user.getPlayers().size() > 0)
            connectUserToPlayer(user);
        return user;
    }

    @Override
    public User connectUserToLeague(User user) {
        String leagueId = user.getLeagues().get(0).getId();
        leagueRepository.findById(leagueId).ifPresent(league -> {
            league.getUsers().add(user);
            leagueRepository.save(league);
        });
        return user;
    }

    @Override
    public User connectUserToPlayer(User user) {
        String playerId = user.getPlayers().get(0).getId();
        playerRepository.findById(playerId).ifPresent(player -> {
            player.setUser(user);
            playerRepository.save(player);
        });
        return user;
    }

    @Override
    public User checkForPendingInvitation(User userFromGoogle) {
        User user = userRepository.findByEmailAndInvitationTokenExists(userFromGoogle.getEmail());
        if (user != null) {
            user.clearInvitationToken();
            user.update(userFromGoogle);
            user.setGoogleId(userFromGoogle.getGoogleId());
            userRepository.save(user);
            user = connectUserToLeagueAndPlayer(user);
        }
        return user;
    }

    @Override
    public User saveOrUpdateUser(User userFromGoogle) {
        User savedUser = userRepository.findByGoogleId(userFromGoogle.getGoogleId());
        if (savedUser != null) {
            savedUser.update(userFromGoogle);
            savedUser = userRepository.save(savedUser);
        } else {
            savedUser = userRepository.save(userFromGoogle);
        }
        return savedUser;
    }

    @Override
    public User saveOrUpdateUser(User userFromGoogle, TimeZone timeZone) {
        User savedUser = userRepository.findByGoogleId(userFromGoogle.getGoogleId());
        if (savedUser != null) {
            savedUser.update(userFromGoogle);
            savedUser = setUserTimezone(savedUser, timeZone);
            savedUser = userRepository.save(savedUser);
        } else {
            userFromGoogle = setUserTimezone(userFromGoogle, timeZone);
            savedUser = userRepository.save(userFromGoogle);
        }
        return savedUser;
    }

    @Override
    public User inviteNewUser(String currentUser, User userToInvite, String originUrl) {
        String token = UUID.randomUUID().toString();
        userToInvite.setInvitationToken(token);
        userRepository.save(userToInvite);
        EmailBuilder emailBuilder = new InviteNewUserEmail(userToInvite.getEmail(), currentUser, originUrl, token);
        sendEmail(emailBuilder);
        userToInvite.clearInvitationToken();
        return userToInvite;
    }

    @Override
    public User inviteExistingUser(String currentUser, User requestUser, String originUrl) {
        League league = requestUser.getLeagues().get(0);
        User userFromDB = userRepository.findByEmail(requestUser.getEmail());
        User invitedUser = connectUserAndLeague(userFromDB.getId(), league.getId());
        if (requestUser.getPlayers() != null && requestUser.getPlayers().size() > 0)
            invitedUser = connectUserAndPlayer(userFromDB.getId(), requestUser.getPlayers().get(0).getId());
        EmailBuilder emailBuilder = new InviteExistingUserEmail(invitedUser.getEmail(), currentUser, originUrl, league);
        sendEmail(emailBuilder);
        return invitedUser;
    }

    @Override
    public User connectUserAndLeague(String userId, String leagueId) {
        Optional<User> user = userRepository.findById(userId);
        Optional<League> league = leagueRepository.findById(leagueId);
        if (user.isPresent() && league.isPresent()) {
            user.get().addLeague(league.get());
            userRepository.save(user.get());
            league.get().addUser(user.get());
            leagueRepository.save(league.get());
        }
        return user.orElse(null);
    }

    @Override
    public User connectUserAndPlayer(String userId, String playerId) {
        Optional<User> user = userRepository.findById(userId);
        Optional<Player> player = playerRepository.findById(playerId);
        if (user.isPresent() && player.isPresent()) {
            user.get().addPlayer(player.get());
            userRepository.save(user.get());
            player.get().setUser(user.get());
            playerRepository.save(player.get());
        }
        return user.orElse(null);
    }

    @Override
    public Player createPlayerForUser(String userId, String leagueId) {
        return userRepository.findById(userId).map(currentUser -> {
            League league = new League(leagueId);
            Player player = new Player(currentUser.getName(), league);
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

    private User setUserTimezone(User user, TimeZone timeZone) {
        if (user.getTimezone() == null) {
            user.setTimezone(DateUtils.getTimezoneOffset(timeZone));
        }
        return user;
    }
}
