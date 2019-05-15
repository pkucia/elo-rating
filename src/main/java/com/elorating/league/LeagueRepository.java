package com.elorating.league;

import com.elorating.user.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeagueRepository extends MongoRepository<LeagueDocument, String> {
    List<LeagueDocument> findByNameLikeIgnoreCase(String name);
    List<LeagueDocument> findByUsersNull();
    LeagueDocument findByIdAndUsers(String id, UserDocument user);
}
