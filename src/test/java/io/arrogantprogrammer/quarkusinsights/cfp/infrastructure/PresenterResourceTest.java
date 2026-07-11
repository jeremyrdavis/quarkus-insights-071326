package io.arrogantprogrammer.quarkusinsights.cfp.infrastructure;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class PresenterResourceTest {

    final String presenterJson = "{\"email\":\"test@example.com\",\"firstName\":\"Test\",\"lastName\":\"Presenter\"}";

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
