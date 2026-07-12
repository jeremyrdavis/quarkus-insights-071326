package io.arrogantprogrammer.quarkusinsights.cfp.domain.services;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.ConferenceSessionFormat;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.ConferenceTrack;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.EmailAddress;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.FormatCode;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.Language;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.Level;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.ProgrammingLanguage;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.SubmissionContext;
import io.arrogantprogrammer.quarkusinsights.cfp.application.SubmissionProposalApplicationService;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.TrackCode;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates.Cfp;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates.SessionProposal;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates.Presenter;
import io.arrogantprogrammer.quarkusinsights.cfp.persistence.CfpRepository;
import io.arrogantprogrammer.quarkusinsights.cfp.persistence.SessionProposalRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@QuarkusTest
public class SubmissionProposalServiceTest {

    private static final UUID CFP_ID = UUID.randomUUID();
    private static final UUID PRESENTER_ID = UUID.randomUUID();

    private static final LocalDate CFP_OPENS = LocalDate.now().minusDays(30);
    private static final LocalDate CFP_CLOSES = LocalDate.now().plusDays(30);

    private static final ConferenceSessionFormat TECHNICAL_SESSION_FORMAT = new ConferenceSessionFormat(
            FormatCode.TECHNICAL_SESSION,
            "Technical Session",
            "A standard technical session",
            Duration.ofMinutes(50));

    private static final ConferenceTrack SERVER_SIDE_JAVA_TRACK = new ConferenceTrack(
            "SERVER_SIDE_JAVA",
            "Server Side Java",
            "Sessions about server side Java");

    @Inject
    SubmissionProposalApplicationService submissionProposalService;

    @InjectMock
    CfpRepository cfpRepository;

    @InjectMock
    SessionProposalRepository sessionProposalRepository;

    @BeforeEach
    public void setup() {
        Cfp cfp = new Cfp(
                CFP_ID,
                CFP_OPENS,
                CFP_CLOSES,
                "Quarkus Insights Conference",
                "https://quarkus.io/insights",
                "A conference about all things Quarkus",
                List.of(TECHNICAL_SESSION_FORMAT),
                List.of(SERVER_SIDE_JAVA_TRACK),
                new EmailAddress("cfp@quarkusinsights.io"));
        when(cfpRepository.findByUUID(CFP_ID)).thenReturn(Optional.of(cfp));

        Presenter presenter = new Presenter(
                PRESENTER_ID,
                new EmailAddress("speaker@quarkusinsights.io"),
                "Jane",
                "Doe");
        SessionProposal sessionProposal = new SessionProposal(
                UUID.randomUUID(),
                "Getting Started with Quarkus",
                "An introduction to building supersonic, subatomic Java applications",
                TECHNICAL_SESSION_FORMAT,
                SERVER_SIDE_JAVA_TRACK,
                Level.BEGINNER,
                Language.ENGLISH,
                presenter,
                "Basic Java knowledge",
                "Intro, live coding, Q&A",
                List.of(new ProgrammingLanguage("Java")));
        when(sessionProposalRepository.findSessionProposalsByPresenterId(PRESENTER_ID))
                .thenReturn(List.of(sessionProposal));
    }

    @Test
    public void testGetSubmissionContext() {

        SubmissionContext result = submissionProposalService.getSubmissionContext(CFP_ID, PRESENTER_ID);
        assertNotNull(result);
        assertEquals(CFP_OPENS, result.cfpOpenDate(), "CFP Open Date should match the mocked Cfp");
        assertEquals(CFP_CLOSES, result.cfpCloseDate(), "CFP Close Date should match the mocked Cfp");
        assertEquals(List.of(TECHNICAL_SESSION_FORMAT), result.formats(), "Formats should match the mocked Cfp");
        assertEquals(List.of(SERVER_SIDE_JAVA_TRACK), result.tracks(), "Tracks should match the mocked Cfp");
        assertNotNull(result.currentSessions(), "Current Sessions should not be null");
        assertEquals(1, result.currentSessions().size(), "There should be one current session for the presenter");
        assertEquals("Getting Started with Quarkus", result.currentSessions().get(0).getTitle());
    }
}
