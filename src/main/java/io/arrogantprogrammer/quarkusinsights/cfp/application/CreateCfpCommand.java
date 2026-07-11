package io.arrogantprogrammer.quarkusinsights.cfp.application;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.ConferenceSessionFormat;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.EmailAddress;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.Track;

import java.time.LocalDate;
import java.util.List;

public record CreateCfpCommand(
        LocalDate cfpOpens,
        LocalDate cfpCloses,
        String conferenceName,
        String conferenceUrl,
        String conferenceDescription,
        List<ConferenceSessionFormat> conferenceSessionFormats,
        List<Track> tracks,
        EmailAddress contactEmailAddress) {
}
