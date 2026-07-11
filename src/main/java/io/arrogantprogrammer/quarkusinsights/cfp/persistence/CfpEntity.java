package io.arrogantprogrammer.quarkusinsights.cfp.persistence;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cfp")
public class CfpEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private LocalDate cfpOpens;

    private LocalDate cfpCloses;

    private String conferenceName;

    private String conferenceUrl;

    @Column(length = 2000)
    private String conferenceDescription;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cfp_id")
    private List<FormatEntity> conferenceSessionFormats = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cfp_id")
    private List<TrackEntity> tracks = new ArrayList<>();

    private String contactEmailAddress;

    public CfpEntity() {
    }

    public CfpEntity(LocalDate cfpOpens, LocalDate cfpCloses, String conferenceName, String conferenceUrl, String conferenceDescription, List<FormatEntity> conferenceSessionFormats, List<TrackEntity> tracks, String contactEmailAddress) {
        this.cfpOpens = cfpOpens;
        this.cfpCloses = cfpCloses;
        this.conferenceName = conferenceName;
        this.conferenceUrl = conferenceUrl;
        this.conferenceDescription = conferenceDescription;
        this.conferenceSessionFormats = conferenceSessionFormats;
        this.tracks = tracks;
        this.contactEmailAddress = contactEmailAddress;
    }

    public UUID getId() {
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

    public List<FormatEntity> getConferenceSessionFormats() {
        return conferenceSessionFormats;
    }

    public List<TrackEntity> getTracks() {
        return tracks;
    }

    public String getContactEmailAddress() {
        return contactEmailAddress;
    }
}
