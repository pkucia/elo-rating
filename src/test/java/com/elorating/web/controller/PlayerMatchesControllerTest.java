//package com.elorating.web.controller;
//
//import com.elorating.league.LeagueDocument;
//import com.elorating.match.MatchDocument;
//import com.elorating.player.PlayerDocument;
//import com.elorating.player.PlayerRepository;
//import com.elorating.match.MatchService;
//import org.hamcrest.Matchers;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//
//import static org.hamcrest.Matchers.is;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
//
//public class PlayerMatchesControllerTest extends BaseControllerTest {
//
//    private static final int RETRIES = 6;
//
//    @Autowired
//    private PlayerRepository playerRepository;
//
//    @Autowired
//    private MatchService matchService;
//
//    private PlayerDocument playerOne;
//
//    private PlayerDocument playerTwo;
//
//    @Before
//    public void setUp() throws Exception {
//        mockMvc = webAppContextSetup(webApplicationContext).build();
//        league = leagueService.save(new LeagueDocument(null, "LeagueDocument"));
//        playerOne = playerRepository.save(new PlayerDocument("PlayerOne", league));
//        playerTwo = playerRepository.save(new PlayerDocument("PlayerTwo", league));
//        Calendar calendar = Calendar.getInstance();
//        for (int i = 0; i < RETRIES; i++) {
//            calendar.add(Calendar.DATE, -5);
//            matchService.save(new MatchDocument(playerOne, playerTwo, 2, 1, calendar.getTime()));
//        }
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        matchService.deleteAll();
//        playerRepository.deleteAll();
//        leagueService.deleteAll();
//    }
//
//    @Test
//    public void testGetPlayerMatches() throws Exception {
//        matchService.save(new MatchDocument(playerOne, playerTwo, 2, 0));
//        matchService.save(new MatchDocument(playerTwo, playerOne));
//        mockMvc.perform(get("/api/players/" + playerOne.getId() + "/matches")
//                .contentType(contentType))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", Matchers.hasSize(2 + RETRIES)));
//        mockMvc.perform(get("/api/players/" + playerOne.getId() + "/matches/?sort=asc")
//                .contentType(contentType))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", Matchers.hasSize(2 + RETRIES)));
//    }
//
//    @Test
//    public void testGetPlayerCompletedMatches() throws Exception {
//        mockMvc.perform(get("/api/players/" + playerOne.getId() + "/completed-matches?page=0")
//                .contentType(contentType))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content", Matchers.hasSize(6)));
//        mockMvc.perform(get("/api/players/" + playerOne.getId() + "/completed-matches?page=1")
//                .contentType(contentType))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content", Matchers.hasSize(0)));
//        mockMvc.perform(get("/api/players/" + playerOne.getId() + "/completed-matches?page=0&pageSize=3")
//                .contentType(contentType))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content", Matchers.hasSize(3)));
//    }
//
//    @Test
//    public void testGetPlayerCompletedMatchesAgainstOpponent() throws Exception {
//        String url = "/api/players/" + playerOne.getId() + "/completed-matches/" + playerTwo.getId() + "?page=0&pageSize=2";
//        mockMvc.perform(get(url)
//                .contentType(contentType))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content", Matchers.hasSize(2)));
//    }
//
//    @Test
//    public void testGetPlayerCompletedMatchesByDate() throws Exception {
//        Calendar calendar = Calendar.getInstance();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        calendar.add(Calendar.DATE, -16);
//        String dateFrom = sdf.format(calendar.getTime());
//        calendar.add(Calendar.DATE, 11);
//        String dateTo = sdf.format(calendar.getTime());
//        String url = "/api/players/" + playerOne.getId() + "/completed-matches-by-date"
//                    + "?from=" + dateFrom + "&to=" + dateTo;
//        mockMvc.perform(get(url)
//                .contentType(contentType))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", Matchers.hasSize(2)));
//        url = "/api/players/" + playerOne.getId() + "/completed-matches-by-date?from=" + dateFrom;
//        mockMvc.perform(get(url)
//                .contentType(contentType))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", Matchers.hasSize(3)));
//        url = "/api/players/" + playerOne.getId() + "/completed-matches-by-date";
//        mockMvc.perform(get(url)
//                .contentType(contentType))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", Matchers.hasSize(6)));
//    }
//
//    @Test
//    public void testGetPlayerScheduledMatches() throws Exception {
//        matchService.save(new MatchDocument(playerOne, playerTwo));
//        matchService.save(new MatchDocument(playerTwo, playerOne));
//        mockMvc.perform(get("/api/players/" + playerOne.getId() + "/scheduled-matches")
//                .contentType(contentType))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", Matchers.hasSize(2)));
//        mockMvc.perform(get("/api/players/" + playerOne.getId() + "/scheduled-matches?sort=asc")
//                .contentType(contentType))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", Matchers.hasSize(2)));
//    }
//
//    @Test
//    public void testGetMatchForecastWithoutDraws() throws Exception {
//        String url = "/api/players/" + playerOne.getId() + "/match-forecast/" + playerTwo.getId();
//        mockMvc.perform(get(url)
//                .contentType(contentType))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].ratingDelta", is(24)))
//                .andExpect(jsonPath("$[1].ratingDelta", is(16)))
//                .andExpect(jsonPath("$[2].ratingDelta", is(-16)))
//                .andExpect(jsonPath("$[3].ratingDelta", is(-24)));
//    }
//
//    @Test
//    public void testGetMatchForecastWithDraws() throws Exception {
//        league.getSettings().setSettingsAllowDraws(true);
//        leagueService.save(league);
//        String url = "/api/players/" + playerOne.getId() + "/match-forecast/" + playerTwo.getId();
//        mockMvc.perform(get(url)
//                .contentType(contentType))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].ratingDelta", is(24)))
//                .andExpect(jsonPath("$[1].ratingDelta", is(16)))
//                .andExpect(jsonPath("$[2].ratingDelta", is(0)))
//                .andExpect(jsonPath("$[3].ratingDelta", is(-16)))
//                .andExpect(jsonPath("$[4].ratingDelta", is(-24)));
//    }
//}
