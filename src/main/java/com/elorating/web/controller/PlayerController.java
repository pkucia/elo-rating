package com.elorating.web.controller;

import com.elorating.match.MatchDocument;
import com.elorating.match.MatchService;
import com.elorating.player.PlayerDocument;
import com.elorating.player.PlayerModel;
import com.elorating.player.PlayerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Pattern;
import java.util.List;

@RestController
@RequestMapping("/api")
@Validated
@Api(value = "players", description = "Players API")
public class PlayerController {

    private static final String USERNAME_REGEX = "[0-9a-zA-Z\\s]+";

    @Autowired
    private PlayerService playerService;

    @Autowired
    private MatchService matchService;

    @CrossOrigin
    @RequestMapping(value = "/leagues/{leagueId}/players", method = RequestMethod.GET)
    @ApiOperation(value = "Get players list", notes = "Return players list by league id")
    public ResponseEntity<List<PlayerDocument>> get(@PathVariable String leagueId) {
        List<PlayerDocument> player = playerService.findByLeagueId(leagueId);
        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value = "/players/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "Get player", notes = "Return player by player id")
    public ResponseEntity<PlayerModel> getById(@PathVariable String id) {
        PlayerModel player = playerService.get(id).orElse(null);
        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value = "/leagues/{leagueId}/active-players-count", method = RequestMethod.GET)
    @ApiOperation(value = "Get active players count", notes = "Return active players count")
    public ResponseEntity<Long> getActiveCount(@PathVariable String leagueId) {
        Long playersCount = playerService.getActivePlayersCountByLeague(leagueId);
        return new ResponseEntity<>(playersCount, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value = "/leagues/{leagueId}/players/ranking", method = RequestMethod.GET)
    @ApiOperation(value = "Get players ranking", notes = "Return active players list by league id")
    public ResponseEntity<List<PlayerDocument>> getRanking(@PathVariable String leagueId) {
        Sort sortByRating = new Sort(Sort.Direction.DESC, "rating");
        List<PlayerDocument> ranking = playerService.getRanking(leagueId, sortByRating);
        return new ResponseEntity<>(ranking, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value = "/leagues/{leagueId}/players", method = RequestMethod.POST)
    @ApiOperation(value = "Create player", notes = "Create player")
    public ResponseEntity<PlayerModel> create(@PathVariable String leagueId, @RequestBody PlayerModel player) {
//        player.setLeague(new LeagueDocument(leagueId)); fixme
        player = playerService.save(player);
        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value = "/leagues/{leagueId}/players/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "Edit player", notes = "Edit player by player id")
    public ResponseEntity<PlayerModel> edit(@PathVariable String id, @RequestBody PlayerModel player) {
        playerService.get(id).ifPresent(currentPlayer -> {
//            player.setLeague(currentPlayer.getLeague()); // fixme
            playerService.save(player);
        });
        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value = "/leagues/{leagueId}/players/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Remove player", notes = "Remove player by player id")
    public ResponseEntity<PlayerDocument> delete(@PathVariable String id) {
        removePlayerFromMatches(id);
        playerService.delete(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void removePlayerFromMatches(String playerId) {
        List<MatchDocument> matches = matchService.findByPlayerId(playerId);
        for (MatchDocument match : matches) {
            match.removePlayerId(playerId);
        }

//        matchService.save(matches); fixme
    }

    @CrossOrigin
    @RequestMapping(value = "/leagues/{leagueId}/players/find-by-username", method = RequestMethod.GET)
    @ApiOperation(value = "Find by username", notes = "Find player by username and league")
    public ResponseEntity<List<PlayerDocument>> findByUsername(@PathVariable String leagueId,
                                                               @Pattern(regexp = USERNAME_REGEX, message = "Incorrect username pattern")
                                @RequestParam String username) {
        List<PlayerDocument> players = playerService.findByLeagueIdAndUsername(leagueId, username);
        return new ResponseEntity<>(players, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value = "/leagues/{leagueId}/players/find-active-by-username", method = RequestMethod.GET)
    @ApiOperation(value = "Find active by username", notes = "Find active player by username and league")
    public ResponseEntity<List<PlayerDocument>> findActiveByUsername(@PathVariable String leagueId,
                                                                     @Pattern(regexp = USERNAME_REGEX, message = "Incorrect username pattern")
                                @RequestParam String username) {
        List<PlayerDocument> players = playerService.findActiveByLeagueIdAndUsername(leagueId, username);
        return new ResponseEntity<>(players, HttpStatus.OK);
    }
}
