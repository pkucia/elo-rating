package com.elorating.web.controller;

import com.elorating.league.LeagueDocument;
import com.elorating.league.LeagueModel;
import com.elorating.league.LeagueService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@Api(value = "Leagues API")
public class LeagueController {

    private static final Logger logger = LoggerFactory.getLogger(LeagueController.class);

    private final LeagueService leagueService;

    @Autowired
    public LeagueController(LeagueService leagueService) {
        this.leagueService = leagueService;
    }

    @CrossOrigin
    @RequestMapping(value = "/leagues/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "Get league", notes = "Return league by league id")
    public ResponseEntity<LeagueModel> get(@PathVariable String id) {
        LeagueModel league = leagueService.get(id).orElse(null);
        return new ResponseEntity<>(league, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value = "/leagues/{id}/settings", method = RequestMethod.GET)
    @ApiOperation(value = "Get league's settings", notes = "Return league's settings by league id")
    public ResponseEntity<LeagueDocument.Settings> getSettings(@PathVariable String id) {
        LeagueDocument.Settings settings = leagueService.getSettings(id);
        if (settings == null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(settings, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value = "/leagues", method = RequestMethod.GET)
    @ApiOperation(value = "Get leagues list", notes = "Get all leagues list")
    public ResponseEntity<List<LeagueModel>> getAllLeagues() {
        List<LeagueModel> leaguesList = leagueService.getAll();

        if (leaguesList.isEmpty()) {
            logger.error("No leagues found");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(leaguesList, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value = "/leagues/find-by-name", method = RequestMethod.GET)
    @ApiOperation(value = "Find leagues by name",
                notes = "Return leagues list filtered by league name")
    public ResponseEntity<List<LeagueDocument>> findByName(@RequestParam String name) {
        List<LeagueDocument> leagues = leagueService.findByName(name);
        return new ResponseEntity<>(leagues, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value = "/leagues", method = RequestMethod.POST)
    @ApiOperation(value = "Create league", notes = "Create new league")
    public ResponseEntity<LeagueModel> create(@RequestBody LeagueModel league) {
        LeagueModel createdLeague = leagueService.save(league);
        if (createdLeague == null) {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        return new ResponseEntity<>(createdLeague, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value = "/leagues/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "Update league", notes = "Update existing league details")
    public ResponseEntity<LeagueDocument> update(@RequestBody LeagueDocument league) {
        leagueService.update(league);
        return new ResponseEntity<>(league, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(value = "/leagues/{id}/delete", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete league", notes = "Delete league by league id")
    public ResponseEntity<LeagueDocument> deleteLeague(@PathVariable("id") String id) {
        leagueService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
