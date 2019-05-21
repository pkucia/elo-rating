package com.elorating.player;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PlayerDocumentTest {

    private static final String PLAYER_ID = "1234";

    private PlayerDocument objectUnderTests;

    @Before
    public void setUp() {
        objectUnderTests = new PlayerDocument();
        objectUnderTests.setId(PLAYER_ID);
    }

    @Test
    public void shouldReturnCorrectExpectedScore() {
        PlayerDocument opponent = new PlayerDocument();
        opponent.setRating(1150);
        objectUnderTests.setRating(1496);

        double expectedScore = objectUnderTests.getExpectedScore(opponent);

        assertEquals(0.879926688, expectedScore,0.000001);
    }

    @Test
    public void shouldHaveDefaultFieldValuesAfterObjectCreation() {
        assertTrue(objectUnderTests.isActive());
        assertEquals(1000, objectUnderTests.getRating());
        assertEquals(0, objectUnderTests.getStatistics().getDraw());
        assertEquals(0, objectUnderTests.getStatistics().getWon());
        assertEquals(0, objectUnderTests.getStatistics().getLost());
        assertNull(objectUnderTests.getStatistics().getLastMatchDate());
    }

    @Test
    public void shouldReturnCorrectKFactorBasedOnCurrentRating() {
        objectUnderTests.setRating(1200);
        assertEquals(32, objectUnderTests.getKFactor());

        objectUnderTests.setRating(2100);
        assertEquals(32, objectUnderTests.getKFactor());

        objectUnderTests.setRating(2101);
        assertEquals(24, objectUnderTests.getKFactor());

        objectUnderTests.setRating(2400);
        assertEquals(24, objectUnderTests.getKFactor());

        objectUnderTests.setRating(2401);
        assertEquals(16, objectUnderTests.getKFactor());
    }

    @Test
    public void shouldUpdateWonStatistics() {
        objectUnderTests.updateStatistics(PLAYER_ID);

        PlayerDocument.Statistics statistics = objectUnderTests.getStatistics();
        assertEquals(1, statistics.getWon());
        assertEquals(0, statistics.getLost());
        assertEquals(0, statistics.getDraw());
    }

    @Test
    public void shouldUpdateLostStatistics() {
        objectUnderTests.updateStatistics(PLAYER_ID);

        PlayerDocument.Statistics statistics = objectUnderTests.getStatistics();
        assertEquals(1, statistics.getWon());
        assertEquals(0, statistics.getLost());
        assertEquals(0, statistics.getDraw());
    }

    @Test
    public void shouldUpdateDrawStatistics() {
        objectUnderTests.updateStatistics(PLAYER_ID);

        PlayerDocument.Statistics statistics = objectUnderTests.getStatistics();
        assertEquals(1, statistics.getWon());
        assertEquals(0, statistics.getLost());
        assertEquals(0, statistics.getDraw());
    }

    @Test
    public void shouldRestoreDrawMatchRating() {
        prepareObjectForRestoreTests();

        objectUnderTests.restoreRating(28, true);

        assertEquals(1225, objectUnderTests.getRating());
        assertEquals(1, objectUnderTests.getStatistics().getDraw());
        assertEquals(2, objectUnderTests.getStatistics().getWon());
        assertEquals(2, objectUnderTests.getStatistics().getLost());
    }

    @Test
    public void shouldRestoreWonMatchRating() {
        prepareObjectForRestoreTests();

        objectUnderTests.restoreRating(28, false);

        assertEquals(1225, objectUnderTests.getRating());
        assertEquals(2, objectUnderTests.getStatistics().getDraw());
        assertEquals(1, objectUnderTests.getStatistics().getWon());
        assertEquals(2, objectUnderTests.getStatistics().getLost());
    }

    @Test
    public void shouldRestoreLostMatchRating() {
        prepareObjectForRestoreTests();

        objectUnderTests.restoreRating(-28, false);

        assertEquals(1281, objectUnderTests.getRating());
        assertEquals(2, objectUnderTests.getStatistics().getDraw());
        assertEquals(2, objectUnderTests.getStatistics().getWon());
        assertEquals(1, objectUnderTests.getStatistics().getLost());
    }


    private void prepareObjectForRestoreTests() {
        objectUnderTests.setRating(1253);
        objectUnderTests.getStatistics().setDraw(2);
        objectUnderTests.getStatistics().setLost(2);
        objectUnderTests.getStatistics().setWon(2);
    }
}