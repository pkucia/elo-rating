package com.elorating.match;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MatchRepository extends MongoRepository<MatchDocument, String> {
    List<MatchDocument> findByLeagueId(String id, Sort sort);

    Page<MatchDocument> findByLeagueIdAndCompletedIsTrue(String id, Pageable pageable);

    List<MatchDocument> findByLeagueIdAndCompletedIsFalse(String id, Sort sort);

    MatchDocument findByIdAndCompletedIsTrue(String id);

    List<MatchDocument> findByCompletedIsFalse();

    @Query(value = "{'$or': [{'playerOne.id': ?0}, {'playerTwo.id': ?0}]}")
    List<MatchDocument> findByPlayerId(String playerId, Sort sort);

    @Query(value = "{'$or': [{'playerOne.id': ?0}, {'playerTwo.id': ?0}]}")
    List<MatchDocument> findByPlayerId(String playerId);

    @Query(value =
            "{'$and': [" +
                "{'completed': true}," +
                "{'$or': [{'playerOne.id': ?0}, {'playerTwo.id': ?0}]}" +
            "]}")
    Page<MatchDocument> findCompletedByPlayerId(String playerId, Pageable pageable);

    @Query(value =
            "{'$and': [" +
                "{'completed': true}," +
                "{'$or': [{'playerOne.id': ?0}, {'playerTwo.id': ?0}]}" +
            "]}")
    List<MatchDocument> findCompletedByPlayerId(String playerId);

    @Query(value =
            "{'$and': [" +
                "{'completed': true}," +
                "{'$or': [{'playerOne.id': ?0}, {'playerTwo.id': ?0}]}" +
            "]}")
    List<MatchDocument> findCompletedByPlayerId(String playerId, Sort sort);

    @Query(value =
            "{'$and': [" +
                "{'completed': true}," +
                "{'$or': [{'playerOne.id': ?0}, {'playerTwo.id': ?0}]}," +
                "{'date': {$gte: ?1}}" +
            "]}")
    List<MatchDocument> findCompletedByPlayerIdAndDate(String playerId, Date from, Sort sort);

    @Query(value =
            "{'$and': [" +
                "{'completed': true}," +
                "{'$or': [{'playerOne.id': ?0}, {'playerTwo.id': ?0}]}," +
                "{'date': {$gte: ?1, $lte: ?2}}" +
            "]}")
    List<MatchDocument> findCompletedByPlayerIdAndDate(String playerId, Date from, Date to, Sort sort);

    @Query(value =
            "{'$and': [" +
                "{'completed': false}," +
                "{'$or': [{'playerOne.id': ?0}, {'playerTwo.id': ?0}]}" +
            "]}")
    List<MatchDocument> findScheduledByPlayerId(String playerId, Sort sort);

    @Query(value =
            "{'$and' : [" +
                "{'completed': true}," +
                "{'$or' : [" +
                    "{'$and': [{'playerOne.id': ?0}, {'playerTwo.id': ?1}]}," +
                    "{'$and': [{'playerOne.id': ?1}, {'playerTwo.id': ?0}]}" +
                "]}" +
            "]}")
    List<MatchDocument> findCompletedByPlayerIds(String playerOneId, String playerTwoId);

    @Query(value =
            "{'$and' : [" +
                "{'completed': true}," +
                "{'$or' : [" +
                    "{'$and': [{'playerOne.id': ?0}, {'playerTwo.id': ?1}]}," +
                    "{'$and': [{'playerOne.id': ?1}, {'playerTwo.id': ?0}]}" +
                "]}" +
            "]}")
    List<MatchDocument> findCompletedByPlayerIds(String playerOneId, String playerTwoId, Sort sort);

    @Query(value =
            "{'$and' : [" +
                "{'completed': true}," +
                "{'$or' : [" +
                    "{'$and': [{'playerOne.id': ?0}, {'playerTwo.id': ?1}]}," +
                    "{'$and': [{'playerOne.id': ?1}, {'playerTwo.id': ?0}]}" +
                "]}" +
            "]}")
    Page<MatchDocument> findCompletedByPlayerIds(String playerOneId, String playerTwoId, Pageable pageable);

    void deleteByLeagueId(String leagueId);
}
