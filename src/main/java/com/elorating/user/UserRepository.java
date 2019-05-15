package com.elorating.user;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<UserDocument, String> {
    UserDocument findByGoogleId(String googleId);
    UserDocument findByEmail(String email);
    List<UserDocument> findByNameLikeIgnoreCase(String name);
    UserDocument findByInvitationToken(String token);
    @Query(value = "" +
            "{" +
                "'email': ?0, " +
                "'invitationToken': {'$exists': true, '$ne': ''}" +
            "}")
    UserDocument findByEmailAndInvitationTokenExists(String email);
}
