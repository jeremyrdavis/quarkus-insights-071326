package io.arrogantprogrammer.quarkusinsights.cfp.infrastructure;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
public class CfpResourceTest {

    final String cfpJson = """
            {
                "cfpOpens": "2026-08-01",
                "cfpCloses": "2026-09-30",
                "conferenceName": "Test Conference 2026",
                "conferenceUrl": "https://testconf.example.com",
                "conferenceDescription": "A comprehensive test conference for software developers",
                "contactEmailAddress": "cfp@testconf.example.com",
                "tracks": [
                    {"name": "Architecture", "description": "System design and architecture"},
                    {"name": "Cloud Native", "description": "Cloud-native technologies"}
                ],
                "formats": [
                    {"name": "Talk", "duration": 45},
                    {"name": "Workshop", "duration": 180}
                ]
            }
            """;

    @Test
    public void testCreateCfpWithValidParameters() {

        // Create
        given()
                .when()
                .with()
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .body(cfpJson)
                .post("/cfp/")
                .then()
                .statusCode(201)
                .body("id", notNullValue());
    }
}
