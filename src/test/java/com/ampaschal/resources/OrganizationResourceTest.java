package com.ampaschal.resources;

import com.ampaschal.enums.DatabaseCollections;
import com.ampaschal.mongo.Organization;
import com.ampaschal.mongo.Users;
import com.ampaschal.repositories.DataRepository;
import com.ampaschal.restartifacts.GetOrganizationListResponse;
import com.ampaschal.restartifacts.GetOrganizationResponse;
import com.ampaschal.restartifacts.ResponseBundleCode;
import io.quarkus.test.junit.QuarkusTest;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static io.smallrye.common.constraint.Assert.assertTrue;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeAll;
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
class OrganizationResourceTest {

    @Inject
    DataRepository dataRepository;

    @BeforeAll
    private void init() {
        Users superAdmin = new Users();
        superAdmin.setUserId("super-admin");
        superAdmin.setEmail("super-admin@ampaschal.com");
        dataRepository.save(superAdmin, DatabaseCollections.USERS.getName(), Users.class);


    }

    @AfterAll
    private void cleanUp() {
        dataRepository.getCollection(DatabaseCollections.USERS.getName()).drop();
        dataRepository.getCollection(DatabaseCollections.ORGANIZATIONS.getName()).drop();
        dataRepository.getCollection(DatabaseCollections.SETTINGS.getName()).drop();

    }

    @Test
    @Order(1)
    void createOrganization() {

        JsonObject json = Json.createObjectBuilder()
                .add("name", "Organization 1")
                .add("rcNumber", "number 1")
                .add("website", "website 1")
                .add("industry", "industry1")
                .add("address", "address")
                .add("city", "city")
                .add("state", "state")
                .add("country", "country")
                .add("employees", "100-500")
                .add("superAdminUserId", "super-admin")
                .add("adminFirstName", "Uche")
                .add("adminLastName", "Peter")
                .add("adminEmail", "admin@ampaschal.com")
                .add("adminPhoneNumber", "+2347012354637")
                .build();

        Response response = given()
                .contentType(ContentType.JSON)
                .body(json.toString())
                .when()
                .post("/organization/sign-up")
                .then()
                .statusCode(200)
                .body("code", equalTo(0))
                .contentType(ContentType.JSON)
                .extract()
                .response();

        response.getBody().prettyPrint();

        Organization organization =
                dataRepository.getOrganizationByNameOrRCNumber("Organization 1", "number 1");

        assertNotNull(organization);
        assertNotNull(organization.getOrgId());

        Users users = dataRepository.getUserByIdentifier("email", "admin@ampaschal.com");
        assertNotNull(users);
    }

    @Test
    @Order(7)
    void createOrganization_failWithSameName() {

        JsonObject json = Json.createObjectBuilder()
                .add("name", "Organization 1")
                .add("rcNumber", "number 2")
                .add("website", "website 1")
                .add("industry", "industry1")
                .add("address", "address")
                .add("city", "city")
                .add("state", "state")
                .add("country", "country")
                .add("employees", "100-500")
                .add("superAdminUserId", "super-admin")
                .add("adminUserId", "super-admin")
                .build();

        Response response = given()
                .contentType(ContentType.JSON)
                .body(json.toString())
                .when()
                .post("/organization/create")
                .then()
                .statusCode(200)
                .body("code", equalTo(-1))
                .contentType(ContentType.JSON)
                .extract()
                .response();

        response.getBody().prettyPrint();
    }

    @Test
    @Order(3)
    void updateOrganization() {

        Organization organization =
                dataRepository.getOrganizationByNameOrRCNumber("Organization 1", "number 1");

        JsonObject json = Json.createObjectBuilder()
                .add("name", "Organization 1")
                .add("rcNumber", "number 1")
                .add("website", "new website 1")
                .add("industry", "industry1")
                .add("address", "address")
                .add("city", "city")
                .add("state", "state")
                .add("country", "country")
                .add("employees", "100-500")
                .add("orgId", organization.getOrgId())
                .build();

        Response response = given()
                .contentType(ContentType.JSON)
                .body(json.toString())
                .when()
                .post("/organization/update")
                .then()
                .statusCode(200)
                .body("code", equalTo(0))
                .contentType(ContentType.JSON)
                .extract()
                .response();

        response.getBody().prettyPrint();

        organization =
                dataRepository.getOrganizationByNameOrRCNumber("Organization 1", "number 1");

        assertNotNull(organization);
        assertEquals("new website 1", organization.getWebsite());

    }

    @Test
    @Order(2)
    void signUpForProduct() {

        Organization organization =
                dataRepository.getOrganizationByNameOrRCNumber("Organization 1", "number 1");
        JsonObject json = Json.createObjectBuilder()
                .add("productId", "product-id")
                .add("orgId", organization.getOrgId())
                .build();
        assertFalse(organization.getProducts() != null && organization.getProducts().contains("product-id"));

        Response response = given()
                .contentType(ContentType.JSON)
                .body(json.toString())
                .when()
                .post("/organization/product/sign-up")
                .then()
                .statusCode(200)
                .body("code", equalTo(0))
                .contentType(ContentType.JSON)
                .extract()
                .response();

        response.getBody().prettyPrint();

        organization =
                dataRepository.getOrganizationByNameOrRCNumber("Organization 1", "number 1");

        assertNotNull(organization);
        assertTrue(organization.getProducts().contains("product-id"));
    }

    @Test
    @Order(4)
    void listOrganizations() {


        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/organization/list")
                .then()
                .statusCode(200)
                .body("code", equalTo(0))
                .contentType(ContentType.JSON)
                .extract()
                .response();

        response.getBody().prettyPrint();
        GetOrganizationListResponse jsonResponse = response.as(GetOrganizationListResponse.class);
        assertEquals(0, jsonResponse.getCode());
        assertEquals(1, jsonResponse.getOrganizations().size());
        assertEquals(1, jsonResponse.getMetaData().getActive());

    }

    @Test
    @Order(5)
    void listOrganizations_searchByAdminEmail() {


        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .queryParam("adminEmail", "admin@ampaschal.com")
                .get("/organization/list")
                .then()
                .statusCode(200)
                .body("code", equalTo(0))
                .contentType(ContentType.JSON)
                .extract()
                .response();

        response.getBody().prettyPrint();
        GetOrganizationListResponse jsonResponse = response.as(GetOrganizationListResponse.class);
        assertEquals(0, jsonResponse.getCode());
        assertEquals(1, jsonResponse.getOrganizations().size());
        assertEquals(1, jsonResponse.getMetaData().getActive());

    }

    @Test
    @Order(6)
    void findOrganization_searchByOrgId() {

        Organization organizationByName = dataRepository.getOrganizationByName("Organization 1");
        assertNotNull(organizationByName);

        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .queryParam("orgId", organizationByName.getOrgId())
                .get("/organization/find")
                .then()
                .statusCode(200)
                .body("code", equalTo(0))
                .contentType(ContentType.JSON)
                .extract()
                .response();

        response.getBody().prettyPrint();
        GetOrganizationResponse jsonResponse = response.as(GetOrganizationResponse.class);
        assertEquals(0, jsonResponse.getCode());
        assertEquals(organizationByName.getRcNumber(), jsonResponse.getOrganization().getRcNumber());

    }

    @Test
    @Order(8)
    void deactivateOrganization() {

        Organization organization =
                dataRepository.getOrganizationByNameOrRCNumber("Organization 1", "number 1");
        assertTrue(organization.isActive());

        JsonObject json = Json.createObjectBuilder()
                .add("adminEmail", "admin@ampaschal.com")
                .add("orgId", organization.getOrgId())
                .build();

        Response response = given()
                .contentType(ContentType.JSON)
                .body(json.toString())
                .when()
                .post("/organization/deactivate")
                .then()
                .statusCode(200)
                .body("code", equalTo(0))
                .contentType(ContentType.JSON)
                .extract()
                .response();

        response.getBody().prettyPrint();

        organization =
                dataRepository.getOrganizationByOrgId(organization.getOrgId());

        assertNotNull(organization);
        assertFalse(organization.isActive());

    }


    @Test
    @Order(9)
    void activateOrganization() {

        Organization organization =
                dataRepository.getOrganizationByNameOrRCNumber("Organization 1", "number 1");
        assertFalse(organization.isActive());

        JsonObject json = Json.createObjectBuilder()
                .add("adminEmail", "admin@ampaschal.com")
                .add("orgId", organization.getOrgId())
                .build();

        Response response = given()
                .contentType(ContentType.JSON)
                .body(json.toString())
                .when()
                .post("/organization/activate")
                .then()
                .statusCode(200)
                .body("code", equalTo(0))
                .contentType(ContentType.JSON)
                .extract()
                .response();

        response.getBody().prettyPrint();

        organization =
                dataRepository.getOrganizationByOrgId(organization.getOrgId());

        assertNotNull(organization);
        assertTrue(organization.isActive());

    }

    @Test
    @Order(10)
    void activateOrganization_failsWhenOrganizationIsActive() {

        Organization organization =
                dataRepository.getOrganizationByNameOrRCNumber("Organization 1", "number 1");

        JsonObject json = Json.createObjectBuilder()
                .add("adminEmail", "admin@ampaschal.com")
                .add("orgId", organization.getOrgId())
                .build();

        Response response = given()
                .contentType(ContentType.JSON)
                .body(json.toString())
                .when()
                .post("/organization/activate")
                .then()
                .statusCode(200)
                .body("code", equalTo(-1))
                .body("description", equalTo(ResponseBundleCode.ORGANIZATION_ALREADY_ACTIVE.getDefaultTemplate()))
                .contentType(ContentType.JSON)
                .extract()
                .response();

        response.getBody().prettyPrint();

        organization =
                dataRepository.getOrganizationByOrgId(organization.getOrgId());

        assertNotNull(organization);
        assertTrue(organization.isActive());

    }
}