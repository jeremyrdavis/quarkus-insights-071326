package io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.*;

import java.util.Collection;

public class ConferenceSession {

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

    public ConferenceSession() {
    }

    public ConferenceSession(java.util.UUID id, String title, String description, ConferenceSessionFormat conferenceSessionFormat, ConferenceTrack conferenceTrack, Level level, Language language, Presenter presenter, String preRequisiteKnowledge, String presentationOutline, Collection<ProgrammingLanguage> programmingLanguagesUsed) {
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

    public static ConferenceSession create(
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
        /*
        TODO:
        Check date
        Check the number of exisiting submissions for this presenter
         */
        var conferenceSession = new ConferenceSession();
        conferenceSession.id = java.util.UUID.randomUUID();
        conferenceSession.title = title;
        conferenceSession.description = description;
        conferenceSession.conferenceSessionFormat = conferenceSessionFormat;
        conferenceSession.conferenceTrack = conferenceTrack;
        conferenceSession.level = level;
        conferenceSession.language = language;
        conferenceSession.presenter = presenter;
        conferenceSession.preRequisiteKnowledge = preRequisiteKnowledge;
        conferenceSession.presentationOutline = presentationOutline;
        conferenceSession.programmingLanguagesUsed = programmingLanguagesUsed;
        return conferenceSession;
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
