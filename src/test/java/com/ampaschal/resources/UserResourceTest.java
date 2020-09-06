package com.ampaschal.resources;

import com.ampaschal.enums.DatabaseCollections;
import com.ampaschal.mongo.Users;
import com.ampaschal.repositories.DataRepository;
import io.quarkus.test.junit.QuarkusTest;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;

@Disabled
@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserResourceTest {

    @Inject
    DataRepository dataRepository;

    @AfterAll
    private void cleanUp() {
        dataRepository.getCollection(DatabaseCollections.USERS.getName()).drop();
    }

    @Test
    @Order(2)
    void signUp() {

        JsonObject user = Json.createObjectBuilder()
                .add("firstName", "Peter")
                .add("lastName", "Uche")
                .add("email", "peter@ampaschal.com")
                .add("phoneNumber", "+2347178788743")
                .add("gender", "MALE")
                .add("nin", "23322111223")
                .add("password", "password")
                .build();

        Response response = given()
                .contentType(ContentType.JSON)
                .body(user.toString())
                .when()
                .post("/users/sign-up")
                .then()
                .statusCode(200)
                .body("code", equalTo(0))
                .contentType(ContentType.JSON)
                .extract()
                .response();

        response.getBody().prettyPrint();

        Users users = dataRepository.getUserByIdentifier("email", "peter@ampaschal.com");

        assertNotNull(users);
        assertNotNull(users.getUserId());
        assertNotEquals("password", users.getCredential().getPassword());
        assertEquals("+2347178788743", users.getPhoneNumber());
    }


}