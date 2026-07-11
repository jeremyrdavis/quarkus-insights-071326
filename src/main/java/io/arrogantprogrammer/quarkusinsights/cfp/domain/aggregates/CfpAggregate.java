package io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.ConferenceSessionFormat;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.Track;

import java.time.LocalDate;
import java.util.List;

public class CfpAggregate {
    
    private LocalDate cfpOpens;

    private LocalDate cfpCloses;
    
    private String conferenceName;
    
    private String conferenceUrl;
    
    private String conferenceDescription;

    private List<ConferenceSessionFormat> conferenceSessionFormats;

    private List<Track> tracks;

    public static CfpAggregate create() {
        return new CfpAggregate();
    }

    public CfpAggregate withCfpOpens(LocalDate cfpOpens) {
        this.cfpOpens = cfpOpens;
        return this;
    }

    public CfpAggregate withCfpCloses(LocalDate cfpCloses) {
        this.cfpCloses = cfpCloses;
        return this;
    }

    public CfpAggregate withFormats(List<ConferenceSessionFormat> allConferenceSessionFormats) {
        this.conferenceSessionFormats = allConferenceSessionFormats;
        return this;
    }

    public CfpAggregate withTracks(List<Track> allTracks) {
        this.tracks = allTracks;
        return this;
    }

    public CfpAggregate withConferenceName(String conferenceName) {
        this.conferenceName = conferenceName;
        return this;
    }

    public CfpAggregate withConferenceUrl(String conferenceUrl) {
        this.conferenceUrl = conferenceUrl;
        return this;
    }

    public CfpAggregate withConferenceDescription(String conferenceDescription) {
        this.conferenceDescription = conferenceDescription;
        return this;
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

}
