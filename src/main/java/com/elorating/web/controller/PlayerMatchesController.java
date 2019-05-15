package com.elorating.web.controller;

import com.elorating.match.MatchDocument;
import com.elorating.player.PlayerMatchesService;
import com.elorating.web.utils.SortUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
@Api(value = "PlayerDocument matches", description = "PlayerDocument matches API")
public class PlayerMatchesController {

    @Autowired
    private PlayerMatchesService playerMatchesService;

    @CrossOrigin
    @RequestMapping(value = "/players/{playerId}/matches", method = RequestMethod.GET)
    @ApiOperation(value = "Get player matches list", notes = "Return matches list by player id")
    public ResponseEntity<List<MatchDocument>> getPlayerMatches(@PathVariable String playerId,
                                                                @RequestParam(required = false) String sort) {
        Sort sortByDate = SortUtils.getSort(sort);
        List<MatchDocument> matches = playerMatchesService.findByPlayerId(playerId, sortByDate);
        return new ResponseEntity<>(matches, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value = "/players/{playerId}/completed-matches", method = RequestMethod.GET)
    @ApiOperation(value = "Get player completed matches page",
            notes = "Return page with player's completed matches by player id")
    public ResponseEntity<Page<MatchDocument>> getPlayerCompletedMatches(@PathVariable String playerId,
                                                                         @RequestParam int page,
                                                                         @RequestParam(defaultValue = "10") int pageSize,
                                                                         @RequestParam(required = false) String sort) {
        Sort sortByDate = SortUtils.getSort(sort);
        PageRequest pageRequest = PageRequest.of(page, pageSize, sortByDate);
        Page<MatchDocument> matches = playerMatchesService.findCompletedByPlayerId(playerId, pageRequest);
        return new ResponseEntity<>(matches, HttpStatus.OK);

    }

    @CrossOrigin
    @RequestMapping(value = "/players/{playerId}/completed-matches/{opponentId}", method = RequestMethod.GET)
    @ApiOperation(value = "Get player completed matches page filtered by opponent",
            notes = "Return page with player's completed matches by player id against opponent")
    public ResponseEntity<Page<MatchDocument>> getPlayerCompletedMatchesAgainstOpponent(
                                            @PathVariable String playerId,
                                            @PathVariable String opponentId,
                                            @RequestParam int page,
                                            @RequestParam(defaultValue = "10") int pageSize,
                                            @RequestParam(required = false) String sort) {
        Sort sortByDate = SortUtils.getSort(sort);
        PageRequest pageRequest = PageRequest.of(page, pageSize, sortByDate);
        Page<MatchDocument> matches = playerMatchesService.findCompletedByPlayerIds(playerId, opponentId, pageRequest);
        return new ResponseEntity<>(matches, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value = "/players/{playerId}/match-forecast/{opponentId}", method = RequestMethod.GET)
    @ApiOperation(value = "Get match forecast against a specific opponent",
            notes = "Return list of matches with all possible scores")
    public ResponseEntity<List<MatchDocument>> getMatchForecast(@PathVariable String playerId,
                                                                @PathVariable String opponentId) {
        List<MatchDocument> matches = playerMatchesService.getMatchForecast(playerId, opponentId);
        return new ResponseEntity<>(matches, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value = "/players/{playerId}/completed-matches-by-date", method = RequestMethod.GET)
    @ApiOperation(value = "Get player completed matches filtered by date",
            notes = "Return list with player's completed matches by player id and date")
    public ResponseEntity<List<MatchDocument>> getPlayerCompletedMatchesByDate(
            @PathVariable String playerId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date from,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date to) {
        List<MatchDocument> matches;
        Sort sort = SortUtils.getSortAscending();
        if (from != null && to != null)
            matches = playerMatchesService.findCompletedByPlayerIdAndDate(playerId, from, to, sort);
        else if (from != null)
            matches = playerMatchesService.findCompletedByPlayerIdAndDate(playerId, from, sort);
        else
            matches = playerMatchesService.findCompletedByPlayerId(playerId, sort);

        return new ResponseEntity<>(matches, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value = "/players/{playerId}/scheduled-matches", method = RequestMethod.GET)
    @ApiOperation(value = "Get player scheduled matches page",
            notes = "Return page with player's scheduled matches by player id")
    public ResponseEntity<List<MatchDocument>> getPlayerScheduledMatches(@PathVariable String playerId,
                                                                         @RequestParam(required = false) String sort) {
        Sort sortByDate = SortUtils.getSort(sort);
        List<MatchDocument> matches = playerMatchesService.findScheduledByPlayerId(playerId, sortByDate);
        return new ResponseEntity<>(matches, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value = "/players/{playerId}/matches/{opponentId}", method = RequestMethod.GET)
    @ApiOperation(value = "Get player matches list against a specific opponent", notes = "Return matches between two players")
    public ResponseEntity<List<MatchDocument>> getPlayerMatchesAgainstOpponent(@PathVariable String playerId, @PathVariable String opponentId,
                                                                               @RequestParam(required = false) String sort) {
        Sort sortByDate = SortUtils.getSort(sort);
        List<MatchDocument> matches = playerMatchesService.findCompletedByPlayerIds(playerId, opponentId, sortByDate);
        return new ResponseEntity<>(matches, HttpStatus.OK);
    }
}
