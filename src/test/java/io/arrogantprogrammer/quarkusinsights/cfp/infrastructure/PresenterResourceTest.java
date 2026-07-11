package io.arrogantprogrammer.quarkusinsights.cfp.infrastructure;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
public class PresenterResourceTest {

    final String presenterJson = "{\"email\":\"test@example.com\",\"firstName\":\"Test\",\"lastName\":\"Presenter\"}";

    @Test
    void testPresenterFlow() {
        // Create
        given()
                .when()
                .with()
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .body(presenterJson)
                .post("/presenters/")
                .then()
                .statusCode(201)
                .body("emailAddress.address", is("test@example.com"))
                .body("id", notNullValue());

        // Get
        given()
                .when()
                .get("/presenters/test@example.com")
                .then()
                .statusCode(200)
                .body("firstName", is("Test"));

        // Update
        String updatedJson = "{\"email\":\"test@example.com\",\"firstName\":\"Updated\",\"lastName\":\"Presenter\"}";
        given()
                .when()
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .body(updatedJson)
                .put("/presenters/test@example.com")
                .then()
                .statusCode(200)
                .body("firstName", is("Updated"));

        // Delete
        given()
                .when()
                .delete("/presenters/test@example.com")
                .then()
                .statusCode(204);

        // Get again should return 404
        given()
                .when()
                .get("/presenters/test@example.com")
                .then()
                .statusCode(404);
    }

    @Test
    void testCreatePresenterEndpoint() {
            given()
                    .when()
                    .with()
                    .header("Content-Type", MediaType.APPLICATION_JSON)
                    .body(presenterJson)
                    .post("/presenters/")
                    .then()
                    .statusCode(201);
        }
}
