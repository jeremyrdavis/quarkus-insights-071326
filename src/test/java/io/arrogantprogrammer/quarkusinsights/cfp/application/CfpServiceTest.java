package io.arrogantprogrammer.quarkusinsights.cfp.application;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.*;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class CfpServiceTest {

    @Inject
    CfpService cfpService;

    @Test
    public void testCreateConferenceSession() {
        EmailAddress email = new EmailAddress("test@example.com");
        ProgrammingLanguage java = new ProgrammingLanguage("Java");
        Format format = Format.create(FormatCode.TECHNICAL_SESSION, "Technical Session", "A technical session");
        Track track = Track.create(TrackCode.ARCHITECTURE, "Architecture", "Architecture track");

        CreateConferenceSessionCommand command = new CreateConferenceSessionCommand(
                "Quarkus Insights",
                "A session about Quarkus",
                format,
                track,
                Level.INTERMEDIATE,
                Language.ENGLISH,
                email,
                "1. Intro, 2. Demo, 3. Q&A",
                List.of(java),
                "Basic Java knowledge");

        ConferenceSessionDTO result = cfpService.createConferenceSession(command);

        assertNotNull(result);
        assertEquals("Quarkus Insights", result.title());
        assertEquals("A session about Quarkus", result.description());
        assertEquals(format, result.format());
        assertEquals(track, result.track());
        assertEquals(Level.INTERMEDIATE, result.level());
        assertEquals(Language.ENGLISH, result.language());
        assertEquals("1. Intro, 2. Demo, 3. Q&A", result.presentationOutline());
        assertEquals(1, result.programmingLanguagesUsed().size());
        assertEquals(java, result.programmingLanguagesUsed().iterator().next());
        assertEquals("Basic Java knowledge", result.preRequisiteKnowledge());
    }

    @Test
    public void testCreateCfp() {
        LocalDate opens = LocalDate.of(2026, 1, 1);
        LocalDate closes = LocalDate.of(2026, 1, 31);
        CreateCfpCommand command = new CreateCfpCommand(opens, closes);

        CfpDTO result = cfpService.createCfp(command);

        assertNotNull(result);
        assertEquals(opens, result.cfpOpens());
        assertEquals(closes, result.cfpCloses());
    }
}
