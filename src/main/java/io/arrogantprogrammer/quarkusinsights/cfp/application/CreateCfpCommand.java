package io.arrogantprogrammer.quarkusinsights.cfp.application;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.ConferenceSessionFormat;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.EmailAddress;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.ConferenceTrack;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public record CreateCfpCommand(
        LocalDate cfpOpens,
        LocalDate cfpCloses,
        String conferenceName,
        String conferenceUrl,
        String conferenceDescription,
        List<ConferenceSessionFormat> conferenceSessionFormats,
        List<ConferenceTrack> conferenceTracks,
        EmailAddress contactEmailAddress) {

    public CreateCfpCommand {
        Objects.requireNonNull(cfpOpens, "Open date must not be null");
        Objects.requireNonNull(cfpCloses, "Close date must not be null");
        Objects.requireNonNull(conferenceName, "Conference name must not be null");
        Objects.requireNonNull(conferenceUrl, "Conference URL must not be null");
        Objects.requireNonNull(conferenceDescription, "Conference description must not be null");
        Objects.requireNonNull(conferenceSessionFormats, "Conference session formats must not be null");
        Objects.requireNonNull(contactEmailAddress, "Contact email address must not be null");
    }
}
