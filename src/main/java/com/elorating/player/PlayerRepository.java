package com.elorating.player;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends MongoRepository<PlayerDocument, String> {
    List<PlayerDocument> findByLeagueId(String id);

    List<PlayerDocument> findByIdNotAndLeagueId(String id, String leagueId);

    Long countByLeagueIdAndActiveIsTrue(String leagueId);

    @Query(value = "{'league.id': ?0, 'active': true}")
    List<PlayerDocument> getRanking(String id, Sort sort);

    List<PlayerDocument> findByLeagueIdAndUsernameLikeIgnoreCase(String leagueId, String username);

    List<PlayerDocument> findByLeagueIdAndActiveIsTrueAndUsernameLikeIgnoreCase(String leagueId, String username);

    List<PlayerDocument> findByLeagueIdAndUsernameRegex(String leagueId, String username);

    List<PlayerDocument> findByLeagueIdAndActiveIsTrueAndUsernameRegex(String leagueId, String username);

    void deleteByLeagueId(String leagueId);
}
