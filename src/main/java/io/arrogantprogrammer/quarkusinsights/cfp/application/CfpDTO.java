package io.arrogantprogrammer.quarkusinsights.cfp.application;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.ConferenceSessionFormat;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.EmailAddress;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.ConferenceTrack;

import java.time.LocalDate;
import java.util.List;

public record CfpDTO(
        java.util.UUID id,
        LocalDate cfpOpens,
        LocalDate cfpCloses,
        String conferenceName,
        String conferenceUrl,
        String conferenceDescription,
        List<ConferenceTrack> conferenceTracks,
        List<ConferenceSessionFormat> conferenceSessionFormats,
        EmailAddress contactEmailAddress) {
}
