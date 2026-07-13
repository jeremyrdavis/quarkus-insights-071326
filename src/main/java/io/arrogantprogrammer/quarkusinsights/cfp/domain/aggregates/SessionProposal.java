package io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.*;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.events.SessionProposalStatusChangedEvent;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public class SessionProposal {

    private UUID id;
    private UUID cfpId;
    private String title;
    private String description;
    private ConferenceSessionFormat conferenceSessionFormat;
    private ConferenceTrack conferenceTrack;
    private Level level;
    private Language language;
    private Presenter presenter;
    private String preRequisiteKnowledge;
    private String presentationOutline;
    private Collection<ProgrammingLanguage> programmingLanguagesUsed;
    private SessionProposalStatus status;

    public SessionProposal() {
    }

    public SessionProposal(UUID id, UUID cfpId, String title, String description, ConferenceSessionFormat conferenceSessionFormat, ConferenceTrack conferenceTrack, Level level, Language language, Presenter presenter, String preRequisiteKnowledge, String presentationOutline, Collection<ProgrammingLanguage> programmingLanguagesUsed, SessionProposalStatus status) {
        this.id = id;
        this.cfpId = cfpId;
        this.title = title;
        this.description = description;
        this.conferenceSessionFormat = conferenceSessionFormat;
        this.conferenceTrack = conferenceTrack;
        this.level = level;
        this.language = language;
        this.presenter = presenter;
        this.preRequisiteKnowledge = preRequisiteKnowledge;
        this.presentationOutline = presentationOutline;
        this.programmingLanguagesUsed = programmingLanguagesUsed;
        this.status = status;
    }

    public static SessionProposal create(
            UUID cfpId,
            SubmissionContext submissionContext,
            String title,
            String description,
            ConferenceSessionFormat conferenceSessionFormat,
            ConferenceTrack conferenceTrack,
            Level level,
            Language language,
            Presenter presenter,
            String preRequisiteKnowledge,
            String presentationOutline,
            Collection<ProgrammingLanguage> programmingLanguagesUsed) {
        LocalDate now = LocalDate.now();
        if (now.isBefore(submissionContext.cfpOpenDate())) {
            throw new IllegalArgumentException("CFP is not open yet");
        } else if (now.isAfter(submissionContext.cfpCloseDate())) {
            throw new IllegalArgumentException("CFP is closed");
        }
        var sessionProposal = new SessionProposal();
        sessionProposal.id = UUID.randomUUID();
        sessionProposal.cfpId = cfpId;
        sessionProposal.title = title;
        sessionProposal.description = description;
        sessionProposal.conferenceSessionFormat = conferenceSessionFormat;
        sessionProposal.conferenceTrack = conferenceTrack;
        sessionProposal.level = level;
        sessionProposal.language = language;
        sessionProposal.presenter = presenter;
        sessionProposal.preRequisiteKnowledge = preRequisiteKnowledge;
        sessionProposal.presentationOutline = presentationOutline;
        sessionProposal.programmingLanguagesUsed = programmingLanguagesUsed;
        sessionProposal.status = SessionProposalStatus.SUBMITTED;
        return sessionProposal;
    }

    public SessionProposalStatusChangedEvent review(SessionProposalStatus newStatus) {
        Objects.requireNonNull(newStatus, "newStatus is required");
        if (newStatus == this.status) {
            throw new IllegalArgumentException("Proposal is already " + newStatus.displayValue);
        }
        SessionProposalStatus previous = this.status;
        this.status = newStatus;
        return new SessionProposalStatusChangedEvent(
                id, cfpId, title,
                presenter != null ? presenter.getEmail() : null,
                previous, newStatus);
    }

    public UUID getId() {
        return id;
    }

    public UUID getCfpId() {
        return cfpId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public ConferenceSessionFormat getFormat() {
        return conferenceSessionFormat;
    }

    public ConferenceTrack getTrack() {
        return conferenceTrack;
    }

    public Level getLevel() {
        return level;
    }

    public Language getLanguage() {
        return language;
    }

    public Presenter getPresenter() {
        return presenter;
    }

    public String getPreRequisiteKnowledge() {
        return preRequisiteKnowledge;
    }

    public String getPresentationOutline() {
        return presentationOutline;
    }

    public Collection<ProgrammingLanguage> getProgrammingLanguagesUsed() {
        return programmingLanguagesUsed;
    }

    public SessionProposalStatus getStatus() {
        return status;
    }
}
