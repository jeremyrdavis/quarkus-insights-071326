package io.arrogantprogrammer.quarkusinsights.cfp.application;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.*;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class CfpServiceTest {

    @Inject
    CfpApplicationService cfpService;

    @Test
    public void testCreateSessionProposal() {
        // CFP 44444444 is open Jul–Sep 2026; use it so SessionProposal.create() doesn't reject the submission
        UUID cfpId = UUID.fromString("44444444-4444-4444-4444-444444444444");
        EmailAddress email = new EmailAddress("test@example.com");
        ProgrammingLanguage java = new ProgrammingLanguage("Java");
        ConferenceSessionFormat conferenceSessionFormat = new ConferenceSessionFormat(FormatCode.TECHNICAL_SESSION, "Technical Session", "A technical session", Duration.ofMinutes(50));
        ConferenceTrack conferenceTrack = new ConferenceTrack("ARCHITECTURE", "Architecture", "Architecture track");

        CreateSessionProposalCommand command = new CreateSessionProposalCommand(
                cfpId,
                "Quarkus Insights",
                "A session about Quarkus",
                conferenceSessionFormat,
                conferenceTrack,
                Level.INTERMEDIATE,
                Language.ENGLISH,
                email,
                "1. Intro, 2. Demo, 3. Q&A",
                List.of(java),
                "Basic Java knowledge");

        SessionProposalDTO result = cfpService.createSessionProposal(command);

        assertNotNull(result);
        assertEquals("Quarkus Insights", result.title());
        assertEquals("A session about Quarkus", result.description());
        assertEquals(conferenceSessionFormat, result.conferenceSessionFormat());
        assertEquals(conferenceTrack, result.conferenceTrack());
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
        EmailAddress email = new EmailAddress("test@example.com");
        CreateCfpCommand command = new CreateCfpCommand(
                opens,
                closes,
                "Test Conference",
                "https://testconf.com",
                "A test conference",
                List.of(new ConferenceSessionFormat(FormatCode.TECHNICAL_SESSION, "Technical Session", "A technical session", Duration.ofMinutes(50))),
                null,
                email);

        CfpDTO result = cfpService.createCfp(command);

        assertNotNull(result);
        assertEquals(opens, result.cfpOpens());
        assertEquals(closes, result.cfpCloses());
        assertEquals("Test Conference", result.conferenceName());
        assertEquals("https://testconf.com", result.conferenceUrl());
        assertEquals("A test conference", result.conferenceDescription());
        assertEquals(email, result.contactEmailAddress());
    }
}
