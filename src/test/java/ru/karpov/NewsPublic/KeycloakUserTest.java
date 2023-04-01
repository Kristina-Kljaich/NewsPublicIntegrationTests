package ru.karpov.NewsPublic;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

/**
 * Requires Docker running on the machine to run without errors
 * Therefore, skipped from pipeline
 */
class KeycloakUserTest extends KeycloakTestContainers {

    @Test
    void givenAuthenticatedUser_whenGetMe_shouldReturnMyInfo() {

        given().header("Authorization", userTokens)
                .when()
                .get("/users/me")
                .then()
                .body("username", equalTo("janedoe"))
                .body("lastname", equalTo("Doe"))
                .body("firstname", equalTo("Jane"))
                .body("email", equalTo("jane.doe@baeldung.com"));

    }
}

