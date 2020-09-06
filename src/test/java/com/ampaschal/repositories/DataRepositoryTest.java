package com.ampaschal.repositories;

import com.ampaschal.enums.DatabaseCollections;
import com.ampaschal.mongo.Users;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.inject.Inject;

@Disabled
@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DataRepositoryTest {

    private String email = "user@ampaschal.com";
    private String userId = "user-unique-id";

    @Inject
    DataRepository dataRepository;

    @BeforeAll
    private void init() {
        Users user = new Users();
        user.setEmail(email);
        user.setUserId(userId);
        dataRepository.save(user, DatabaseCollections.USERS.getName(), Users.class);
    }

    @AfterAll
    private void cleanUp() {
        dataRepository.getCollection(DatabaseCollections.USERS.getName()).drop();
    }


    @Test
    void getUserEmailFromUserId() {

        String email = dataRepository.getUserEmailFromUserId(this.userId);

        assertEquals(this.email, email);
    }

    @Test
    void getUserIdFromEmail() {

        String userId = dataRepository.getUserIdFromEmail(this.email);

        assertEquals(this.userId, userId);
    }
}