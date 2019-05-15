package com.elorating.model;

import com.elorating.match.MatchDocument;
import com.elorating.player.PlayerDocument;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MatchDocumentTest {

    private PlayerDocument playerOne;
    private PlayerDocument playerTwo;
    private MatchDocument match;

    @Before
    public void setUp() throws Exception {
        playerOne = new PlayerDocument("PlayerDocument one");
        playerOne.setId("111");
        playerTwo = new PlayerDocument("PlayerDocument two");
        playerTwo.setId("222");
        match = new MatchDocument(playerOne, playerTwo);
    }

    @Test
    public void testIsCompletedIsFalse() throws Exception {
        Assert.assertFalse(match.isCompleted());
    }

    @Test
    public void testIsCompletedIsTrueFirst() throws Exception {
        match.setScore(playerOne, 0);
        match.setScore(playerTwo, 2);
        Assert.assertTrue(match.isCompleted());
    }

    @Test
    public void testIsCompletedIsTrueSecond() throws Exception {
        match.setScore(playerOne, 2);
        match.setScore(playerTwo, 0);
        Assert.assertTrue(match.isCompleted());
    }

    @Test
    public void testIsCompleteInvalidScore() throws Exception {
        match.setScore(playerOne, 3);
        Assert.assertFalse(match.isCompleted());
    }

    @Test
    public void testGetWinnerIdPlayerOne() {
        match.setScore(playerOne, 2);
        match.setScore(playerTwo, 1);
        Assert.assertEquals(playerOne.getId(), match.getWinnerId());
    }

    @Test
    public void testGetWinnerIdPlayerTwo() {
        match.setScore(playerOne, 0);
        match.setScore(playerTwo, 2);
        Assert.assertEquals(playerTwo.getId(), match.getWinnerId());
    }

    @Test
    public void testGetWinnerIdNull() {
        Assert.assertNull(match.getWinnerId());
    }

    @Test
    public void testGetLooserIdPlayerOne() {
        match.setScore(playerOne, 1);
        match.setScore(playerTwo, 2);
        Assert.assertEquals(playerOne.getId(), match.getLooserId());
    }

    @Test
    public void testGetLooserIdPlayerTwo() {
        match.setScore(playerOne, 2);
        match.setScore(playerTwo, 0);
        Assert.assertEquals(playerTwo.getId(), match.getLooserId());
    }

    @Test
    public void testIsDrawIsTrue() {
        match.setScore(playerOne, 3);
        match.setScore(playerTwo, 3);
        Assert.assertTrue(match.isDraw());
    }

    @Test
    public void testIsDrawIsFalse() {
        match.setScore(playerOne, 2);
        match.setScore(playerTwo, 4);
        Assert.assertFalse(match.isDraw());
    }

    @Test
    public void testGetLooserIdNull() {
        Assert.assertNull(match.getLooserId());
    }
}