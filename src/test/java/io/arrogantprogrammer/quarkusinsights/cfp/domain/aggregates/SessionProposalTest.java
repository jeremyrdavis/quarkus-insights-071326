package io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.ConferenceSessionFormat;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.ConferenceTrack;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.EmailAddress;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.FormatCode;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.Language;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.Level;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.ProgrammingLanguage;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.SessionProposalStatus;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.SubmissionContext;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SessionProposalTest {

    private static SessionProposal submittedProposal() {
        ConferenceSessionFormat format = new ConferenceSessionFormat(
                FormatCode.TECHNICAL_SESSION, "Technical", "Desc", Duration.ofMinutes(50));
        ConferenceTrack track = new ConferenceTrack("ARCHITECTURE", "Architecture", "Desc");
        SubmissionContext context = new SubmissionContext(
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(30),
                List.of(format), List.of(track), List.of());
        return SessionProposal.create(
                UUID.randomUUID(), context, "Title", "Abstract", format, track,
                Level.BEGINNER, Language.ENGLISH,
                Presenter.create(new EmailAddress("p@example.com"), "Jane", "Doe"),
                "None", "Outline", List.of(new ProgrammingLanguage("Java")));
    }

    @Test
    void acceptChangesSubmittedToAccepted() {
        SessionProposal proposal = submittedProposal();
        assertEquals(SessionProposalStatus.SUBMITTED, proposal.getStatus());
        proposal.accept();
        assertEquals(SessionProposalStatus.ACCEPTED, proposal.getStatus());
    }

    @Test
    void acceptingAnAlreadyAcceptedProposalFails() {
        SessionProposal proposal = submittedProposal();
        proposal.accept();
        assertThrows(IllegalStateException.class, proposal::accept);
    }

    @Test
    void declineChangesStatusToDeclined() {
        SessionProposal proposal = submittedProposal();
        proposal.decline();
        assertEquals(SessionProposalStatus.DECLINED, proposal.getStatus());
    }

    @Test
    void waitlistChangesStatusToWaitlisted() {
        SessionProposal proposal = submittedProposal();
        proposal.waitlist();
        assertEquals(SessionProposalStatus.WAITLISTED, proposal.getStatus());
    }
}
