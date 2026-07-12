package io.arrogantprogrammer.quarkusinsights.cfp.infrastructure;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.ConferenceSessionFormat;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.ConferenceTrack;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

record CfpParameters(
        @NotNull(message = "CFP ID is required") UUID cfpId,
        @NotNull(message = "CFP opening date is required") LocalDate cfpOpens,
        @NotNull(message = "CFP closing date is required") LocalDate cfpCloses,
        @NotNull(message = "Conference name is required") String conferenceName,
        @NotNull(message = "Conference url is required") String conferenceUrl,
        @NotNull(message = "Conference description is required") String conferenceDescription,
        @NotNull(message = "Contact email address is required") String contactEmailAddress,
        List<ConferenceTrack> conferenceTracks,
        @NotNull(message = "At least 1 conference format is required") List<ConferenceSessionFormat> formats) {
}
