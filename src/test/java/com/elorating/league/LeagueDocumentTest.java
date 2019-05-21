package com.elorating.league;

import com.elorating.user.UserDocument;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class LeagueDocumentTest {

    private LeagueDocument objectUnderTests;

    @Before
    public void setUp() {
        objectUnderTests = new LeagueDocument();
    }

    @Test
    public void shouldHaveDefaultSettingsAfterObjectCreation() {
        LeagueDocument.Settings settings = objectUnderTests.getSettings();
        assertFalse(settings.isAllowDraws());
        assertEquals(2, settings.getMaxScore());
    }

    @Test
    public void shouldAddUserToList() {
        objectUnderTests.addUser(new UserDocument());
        assertEquals(1, objectUnderTests.getUsers().size());
    }
}