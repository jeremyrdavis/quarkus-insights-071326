package io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.ConferenceSessionFormat;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.ConferenceTrack;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.EmailAddress;

import java.time.LocalDate;
import java.util.List;

public class Cfp {

    private java.util.UUID id;

    private LocalDate cfpOpens;

    private LocalDate cfpCloses;
    
    private String conferenceName;
    
    private String conferenceUrl;
    
    private String conferenceDescription;

    private List<ConferenceSessionFormat> conferenceSessionFormats;

    private List<ConferenceTrack> conferenceTracks;

    private EmailAddress contactEmailAddress;

    public Cfp() {
    }

    public Cfp(java.util.UUID id, LocalDate cfpOpens, LocalDate cfpCloses, String conferenceName, String conferenceUrl, String conferenceDescription, List<ConferenceSessionFormat> conferenceSessionFormats, List<ConferenceTrack> conferenceTracks, EmailAddress contactEmailAddress) {
        this.id = id;
        this.cfpOpens = cfpOpens;
        this.cfpCloses = cfpCloses;
        this.conferenceName = conferenceName;
        this.conferenceUrl = conferenceUrl;
        this.conferenceDescription = conferenceDescription;
        this.conferenceSessionFormats = conferenceSessionFormats == null ? new java.util.ArrayList<>() : conferenceSessionFormats;
        this.conferenceTracks = conferenceTracks == null ? new java.util.ArrayList<>() : conferenceTracks;
        this.contactEmailAddress = contactEmailAddress;
    }

    public static Cfp create(
            LocalDate cfpOpens,
            LocalDate cfpCloses,
            String conferenceName,
            String conferenceUrl,
            String conferenceDescription,
            List<ConferenceSessionFormat> conferenceSessionFormats,
            List<ConferenceTrack> conferenceTracks,
            EmailAddress contactEmailAddress) {
        Cfp cfp = new Cfp();
        cfp.id = java.util.UUID.randomUUID();
        cfp.cfpOpens = cfpOpens;
        cfp.cfpCloses = cfpCloses;
        cfp.conferenceName = conferenceName;
        cfp.conferenceUrl = conferenceUrl;
        cfp.conferenceDescription = conferenceDescription;
        cfp.conferenceSessionFormats = conferenceSessionFormats == null ? new java.util.ArrayList<>() : conferenceSessionFormats;
        cfp.conferenceTracks = conferenceTracks == null ? new java.util.ArrayList<>() : conferenceTracks;
        cfp.contactEmailAddress = contactEmailAddress;
        return cfp;
    }

    public java.util.UUID getId() {
        return id;
    }

    public LocalDate getCfpOpens() {
        return cfpOpens;
    }

    public LocalDate getCfpCloses() {
        return cfpCloses;
    }

    public String getConferenceName() {
        return conferenceName;
    }

    public String getConferenceUrl() {
        return conferenceUrl;
    }

    public String getConferenceDescription() {
        return conferenceDescription;
    }

    public EmailAddress getContactEmailAddress() {
        return contactEmailAddress;
    }

    public List<ConferenceSessionFormat> getConferenceSessionFormats() {
        return conferenceSessionFormats;
    }

    public List<ConferenceTrack> getTracks() {
        return conferenceTracks;
    }

}
