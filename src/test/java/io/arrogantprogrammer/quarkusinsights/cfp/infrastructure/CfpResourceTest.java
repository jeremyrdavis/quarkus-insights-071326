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
                "conferenceTracks": [
                    {"trackCode": "ARCHITECTURE", "title": "Architecture", "description": "System design and architecture"},
                    {"trackCode": "CLOUD", "title": "Cloud Native", "description": "Cloud-native technologies"}
                ],
                "formats": [
                    {"formatCode": "TECHNICAL_SESSION", "title": "Talk", "description": "A standard talk", "duration": "PT45M"},
                    {"formatCode": "HANDS_ON_LAB", "title": "Workshop", "description": "A hands-on workshop", "duration": "PT3H"}
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
