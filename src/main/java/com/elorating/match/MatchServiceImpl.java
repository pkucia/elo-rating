package com.elorating.match;

import com.elorating.common.AbstractCrudService;
import com.elorating.email.EmailGenerator;
import com.elorating.email.EmailService;
import com.elorating.player.PlayerDocument;
import com.elorating.player.PlayerRepository;
import com.elorating.web.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
class MatchServiceImpl extends AbstractCrudService<MatchDocument, MatchRepository> implements MatchService {

    private final PlayerRepository playerRepository;
    private final EmailService emailService;
    private final EmailGenerator emailGenerator;

    @Autowired
    public MatchServiceImpl(MatchRepository matchRepository, PlayerRepository playerRepository,
                            EmailService emailService, EmailGenerator emailGenerator) {
        super(matchRepository);
        this.playerRepository = playerRepository;
        this.emailService = emailService;
        this.emailGenerator = emailGenerator;
    }

    @Override
    public List<MatchDocument> saveAndNotify(List<MatchDocument> matches, String originUrl) {
        for (MatchDocument match : matches) {
            saveAndNotify(match, originUrl);
        }
        return matches;
    }

    @Override
    public MatchDocument saveAndNotify(MatchDocument match, String originUrl) {
        boolean update = checkIfMatchToUpdate(match);
        match = save(match);
        match = fulfillPlayersInfo(match);
        if (update) {
            this.emailService.sendEmails(emailGenerator.generateEmails(match, emailGenerator.EDIT_MATCH, originUrl));
        } else {
            this.emailService.sendEmails(emailGenerator.generateEmails(match, emailGenerator.SCHEDULE_MATCH, originUrl));
        }
        return match;
    }

    @Override
    public void deleteByIdWithNotification(String id, String originUrl) {
        repository.findById(id).ifPresent(matchToDelete -> {
            repository.deleteById(id);
            matchToDelete = fulfillPlayersInfo(matchToDelete);
            if (!repository.findById(id).isPresent()) {
                this.emailService.sendEmails(emailGenerator.generateEmails(matchToDelete, emailGenerator.CANCEL_MATCH, originUrl));
            }
        });
    }

    @Override
    public MatchDocument saveMatchWithPlayers(MatchDocument match) {
        Elo elo = new Elo(match);
        match.getPlayerOne().setRating(elo.getPlayerOneRating());
        match.getPlayerTwo().setRating(elo.getPlayerTwoRating());
        match.setRatingDelta(elo.getMatch().getRatingDelta());
        updatePlayer(match.getPlayerOne(), match.getWinnerId());
        updatePlayer(match.getPlayerTwo(), match.getWinnerId());
        match.setCompleted();
        match.setDate(new Date());
        return save(match);
    }

    private void updatePlayer(PlayerDocument player, String winnerId) {
        playerRepository.findById(player.getId()).ifPresent(playerToUpdate -> {
            playerToUpdate.setRating(player.getRating());
            playerToUpdate.updateStatistics(winnerId);
            playerRepository.save(playerToUpdate);
        });
    }

    @Override
    public List<MatchDocument> findByLeagueId(String leagueId, Sort sortByDate) {
        return repository.findByLeagueId(leagueId, sortByDate);
    }

    @Override
    public Page<MatchDocument> findByLeagueIdAndCompletedIsTrue(String leagueId, Pageable pageRequest) {
        return repository.findByLeagueIdAndCompletedIsTrue(leagueId, pageRequest);
    }

    @Override
    public List<MatchDocument> findByLeagueIdAndCompletedIsFalse(String leagueId, Sort sortByDate) {
        return repository.findByLeagueIdAndCompletedIsFalse(leagueId, sortByDate);
    }

    @Override
    public List<MatchDocument> findByPlayerId(String playerId) {
        return repository.findByPlayerId(playerId);
    }

    @Override
    public List<MatchDocument> findByCompletedIsFalse() {
        return repository.findByCompletedIsFalse();
    }

    @Override
    public List<MatchDocument> rescheduleMatchesInLeague(String leagueId, int minutes,
                                                         Sort sort, String originUrl) {
        List<MatchDocument> matchesInQueue = repository.findByLeagueIdAndCompletedIsFalse(leagueId, sort);
        List<MatchDocument> matchesToReschedule = new ArrayList<>(matchesInQueue.size());

        Date rescheduleTime = new Date();
        for (int i = 0; i < matchesInQueue.size(); i++) {
            MatchDocument match = matchesInQueue.get(i);
            if (i == 0 && match.getDate().getTime() <= rescheduleTime.getTime()) {
                match.setDate(DateUtils.adjustTimeByMinutesIntoFuture(match.getDate(), minutes));
                matchesToReschedule.add(match);
            } else {
                rescheduleTime = DateUtils.adjustTimeByMinutesIntoFuture(matchesInQueue.get(i - 1).getDate(), minutes);
                if (match.getDate().getTime() < rescheduleTime.getTime()) {
                    match.setDate(rescheduleTime);
                    matchesToReschedule.add(match);
                }
            }
        }

        saveAndNotify(matchesToReschedule, originUrl);
        return repository.findByLeagueIdAndCompletedIsFalse(leagueId, sort);
    }

    @Override
    public boolean checkIfCompleted(MatchDocument match) {
        if (match.getId() != null && match.getId().length() > 0) {
            MatchDocument matchToCheck = repository.findByIdAndCompletedIsTrue(match.getId());
            return matchToCheck != null;
        }
        return false;
    }

    private boolean checkIfMatchToUpdate(MatchDocument match) {
        return match.getId() != null ? true : false;
    }

    private MatchDocument fulfillPlayersInfo(MatchDocument match) {
        Optional<PlayerDocument> playerOne = playerRepository.findById(match.getPlayerOne().getId());
        match.setPlayerOne(playerOne.orElseGet(PlayerDocument::new));
        Optional<PlayerDocument> playerTwo = playerRepository.findById(match.getPlayerTwo().getId());
        match.setPlayerTwo(playerTwo.orElseGet(PlayerDocument::new));
        return match;
    }
}
