package io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.*;

import java.time.LocalDate;
import java.util.Collection;

public class SessionProposal {

    private java.util.UUID id;

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

    public SessionProposal() {
    }

    public SessionProposal(java.util.UUID id, String title, String description, ConferenceSessionFormat conferenceSessionFormat, ConferenceTrack conferenceTrack, Level level, Language language, Presenter presenter, String preRequisiteKnowledge, String presentationOutline, Collection<ProgrammingLanguage> programmingLanguagesUsed) {
        this.id = id;
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
    }

    public static SessionProposal create(
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
        sessionProposal.id = java.util.UUID.randomUUID();
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
        return sessionProposal;
    }

    public java.util.UUID getId() {
        return id;
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
}
