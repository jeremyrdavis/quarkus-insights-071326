package io.arrogantprogrammer.quarkusinsights.cfp.application;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.ConferenceSessionFormat;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.ConferenceTrack;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.EmailAddress;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public record UpdateCfpCommand(
        LocalDate cfpOpens,
        LocalDate cfpCloses,
        String conferenceName,
        String conferenceUrl,
        String conferenceDescription,
        List<ConferenceSessionFormat> conferenceSessionFormats,
        List<ConferenceTrack> conferenceTracks,
        EmailAddress contactEmailAddress) {

    public UpdateCfpCommand {
        Objects.requireNonNull(cfpOpens, "Open date must not be null");
        Objects.requireNonNull(cfpCloses, "Close date must not be null");
        Objects.requireNonNull(conferenceName, "Conference name must not be null");
        Objects.requireNonNull(conferenceUrl, "Conference URL must not be null");
        Objects.requireNonNull(conferenceDescription, "Conference description must not be null");
        Objects.requireNonNull(contactEmailAddress, "Contact email address must not be null");
    }
}
