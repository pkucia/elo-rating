package com.elorating.web;

import com.elorating.league.League;
import com.elorating.league.LeagueSettings;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

public class LeagueControllerTest extends BaseControllerTest {

    private static final int RETIRES = 5;

    @Before
    public void setUp() {
        mockMvc = webAppContextSetup(webApplicationContext).build();
        league = new League("testID1", "TestLeague");
        LeagueSettings settings = new LeagueSettings();
        settings.setAllowDraws(true);
        settings.setMaxScore(8);
        league.setSettings(settings);
        leagueService.save(league);

        for (int i = 0; i < RETIRES; i++) {
            leagueService.save(new League(null, "League_" + i));
        }
    }

    @After
    public void tearDown() {
        leagueService.deleteAll();
    }

    @Test
    public void testGet() throws Exception {
        mockMvc.perform(get("/api/leagues/" + league.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(league.getId())))
                .andExpect(jsonPath("$.name", is(league.getName())));
    }

    @Test
    public void testGetSettings() throws Exception {
        mockMvc.perform(get("/api/leagues/" + league.getId() + "/settings")
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maxScore", is(league.getSettings().getMaxScore())))
                .andExpect(jsonPath("$.allowDraws", is(league.getSettings().isAllowDraws())));
    }

    @Test
    public void testGetAll() throws Exception {
        mockMvc.perform(get("/api/leagues/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(RETIRES + 1)));
    }

    @Test
    public void testFindByName() throws Exception {
        mockMvc.perform(get("/api/leagues/find-by-name?name=Tes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void testCreate() throws Exception {
        String leagueJson = objectMapper.writeValueAsString(new League(null, "New league"));
        mockMvc.perform(post("/api/leagues")
                .content(leagueJson)
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("New league")));
    }

    @Test
    public void testUpdate() throws Exception {
        league.setName("Updated league");
        league.getSettings().setMaxScore(5);
        league.getSettings().setAllowDraws(true);
        String leagueJson = objectMapper.writeValueAsString(league);
        String url = "/api/leagues/" + league.getId();
        mockMvc.perform(put(url)
                .content(leagueJson)
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated league")))
                .andExpect(jsonPath("$.settings.maxScore", is(5)))
                .andExpect(jsonPath("$.settings.allowDraws", is(true)));
    }
}