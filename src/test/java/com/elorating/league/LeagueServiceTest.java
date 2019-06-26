package com.elorating.league;

import com.elorating.user.UserDocument;
import com.elorating.user.UserModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LeagueServiceTest {

    @Mock
    private LeagueRepository repository;
    private LeagueDocument leagueDocument;

    private LeagueService objectUnderTests;

    @Before
    public void setUp() {
        objectUnderTests = new LeagueServiceImpl(repository);
        leagueDocument = new LeagueDocument();
        leagueDocument.setId("league111");
        leagueDocument.setName("Test League");
        leagueDocument.getSettings().setAllowDraws(true);
        leagueDocument.getSettings().setMaxScore(5);
        UserDocument userDocument = new UserDocument();
        leagueDocument.addUser(userDocument);
    }

    @Test
    public void shouldGetReturnLeagueModel() {
        given(repository.findById(eq("league111"))).willReturn(Optional.of(leagueDocument));

        Optional<LeagueModel> leagueOptional = objectUnderTests.get("league111");

        verify(repository, only()).findById(eq("league111"));
        leagueOptional.ifPresentOrElse(leagueModel -> {
            assertEquals(leagueDocument.getId(), leagueModel.getId());
            assertEquals(leagueDocument.getName(), leagueModel.getName());
            assertEquals(leagueDocument.getUsers().size(), leagueModel.getUsers().size());
            assertEquals(leagueDocument.getSettings().getMaxScore(), leagueModel.getSettingsMaxScore());
            assertEquals(leagueDocument.getSettings().isAllowDraws(), leagueModel.isSettingsAllowDraws());
        }, Assert::fail);
    }

    @Test
    public void shouldGetLeagueReturnOptionalEmptyWhenLeagueDoesNotExist() {
        given(repository.findById(eq("league111"))).willReturn(Optional.empty());

        Optional<LeagueModel> leagueOptional = objectUnderTests.get("league111");

        verify(repository, only()).findById(eq("league111"));
        assertFalse(leagueOptional.isPresent());
    }

    @Test
    public void shouldGetAllReturnModelsList() {
        given(repository.findAll()).willReturn(Collections.singletonList(leagueDocument));

        List<LeagueModel> leagues = objectUnderTests.getAll();

        verify(repository, only()).findAll();
        assertEquals(1, leagues.size());
        leagues.stream().findFirst().ifPresentOrElse(leagueModel -> {
            assertEquals(leagueDocument.getId(), leagueModel.getId());
            assertEquals(leagueDocument.getName(), leagueModel.getName());
            assertEquals(leagueDocument.getUsers().size(), leagueModel.getUsers().size());
            assertEquals(leagueDocument.getSettings().getMaxScore(), leagueModel.getSettingsMaxScore());
            assertEquals(leagueDocument.getSettings().isAllowDraws(), leagueModel.isSettingsAllowDraws());
        }, Assert::fail);
    }

    @Test
    public void shouldGetAllReturnEmptyListWhenLeaguesDoesNotExist() {
        given(repository.findAll()).willReturn(Collections.emptyList());

        List<LeagueModel> leagues = objectUnderTests.getAll();

        verify(repository, only()).findAll();
        assertTrue(leagues.isEmpty());
    }

    @Test
    public void shouldDeleteLeagueCallRepositoryMethodAndDeleteAlsoUsersPlayersAndMatches() {
        // todo
        fail();
    }

    @Test
    public void shouldSaveLeagueCallRepositoryMethodAndReturnLeagueModel() {
        LeagueModel leagueModel = new LeagueModel();
        leagueModel.setName(leagueDocument.getName());
        leagueModel.setSettingsAllowDraws(leagueDocument.getSettings().isAllowDraws());
        leagueModel.setSettingsMaxScore(leagueDocument.getSettings().getMaxScore());
        leagueModel.setUsers(Collections.singletonList(new UserModel()));
        given(repository.save(any(LeagueDocument.class))).willReturn(leagueDocument);

        LeagueModel savedLeague = objectUnderTests.save(leagueModel);

        verify(repository, only()).save(any(LeagueDocument.class));
        assertEquals(leagueDocument.getId(), savedLeague.getId());
        assertEquals(leagueDocument.getName(), savedLeague.getName());
        assertEquals(leagueDocument.getUsers().size(), savedLeague.getUsers().size());
        assertEquals(leagueDocument.getSettings().getMaxScore(), savedLeague.getSettingsMaxScore());
        assertEquals(leagueDocument.getSettings().isAllowDraws(), savedLeague.isSettingsAllowDraws());
    }

    @Test
    public void shouldFindByNameReturnModelsList() {
        given(repository.findByNameLikeIgnoreCase(eq(leagueDocument.getName())))
                .willReturn(Collections.singletonList(leagueDocument));

        List<LeagueModel> leagueModels = objectUnderTests.findByName(leagueDocument.getName());

        verify(repository, only()).findByNameLikeIgnoreCase(eq(leagueDocument.getName()));
        assertEquals(1, leagueModels.size());
        Optional<LeagueModel> leagueOptional = leagueModels.stream().findFirst();
        leagueOptional.ifPresentOrElse(leagueModel -> {
            assertEquals(leagueDocument.getId(), leagueModel.getId());
            assertEquals(leagueDocument.getName(), leagueModel.getName());
            assertEquals(leagueDocument.getUsers().size(), leagueModel.getUsers().size());
            assertEquals(leagueDocument.getSettings().getMaxScore(), leagueModel.getSettingsMaxScore());
            assertEquals(leagueDocument.getSettings().isAllowDraws(), leagueModel.isSettingsAllowDraws());
        }, Assert::fail);
    }

    @Test
    public void shouldUpdateCallRepositoryMethodAndReturnUpdatedModel() {
        LeagueModel leagueModel = new LeagueModel();
        leagueModel.setId(leagueDocument.getId());
        leagueModel.setName("UpdatedName");
        leagueModel.setSettingsMaxScore(8);
        leagueModel.setSettingsAllowDraws(true);
        given(repository.findById(eq(leagueModel.getId()))).willReturn(Optional.of(leagueDocument));
        given(repository.save(leagueDocument)).willReturn(leagueDocument);

        LeagueModel updatedModel = objectUnderTests.update(leagueModel);

        verify(repository, times(1)).findById(eq(leagueModel.getId()));
        verify(repository, times(1)).save(any(LeagueDocument.class));
        assertEquals(leagueModel.getId(), updatedModel.getId());
        assertEquals(leagueModel.getName(), updatedModel.getName());
        assertEquals(leagueModel.getSettingsMaxScore(), updatedModel.getSettingsMaxScore());
        assertEquals(leagueModel.isSettingsAllowDraws(), updatedModel.isSettingsAllowDraws());
        assertEquals(leagueDocument.getUsers().size(), updatedModel.getUsers().size());
    }

    @Test
    public void shouldFindUnassignedLeaguesReturnLeagueModels() {
        leagueDocument.setUsers(Collections.emptyList());
        given(repository.findByUsersNull()).willReturn(Collections.singletonList(leagueDocument));

        List<LeagueModel> leagueModels = objectUnderTests.findUnassignedLeagues();

        verify(repository, only()).findByUsersNull();
        assertEquals(1, leagueModels.size());
        leagueModels.stream().findFirst().ifPresentOrElse(leagueModel -> {
            assertEquals(leagueDocument.getId(), leagueModel.getId());
            assertEquals(leagueDocument.getName(), leagueModel.getName());
            assertTrue(leagueModel.getUsers().isEmpty());
            assertEquals(leagueDocument.getSettings().getMaxScore(), leagueModel.getSettingsMaxScore());
            assertEquals(leagueDocument.getSettings().isAllowDraws(), leagueModel.isSettingsAllowDraws());
        }, Assert::fail);
    }
}