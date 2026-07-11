package io.arrogantprogrammer.quarkusinsights.cfp.application;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.EmailAddress;

import java.time.LocalDate;
import java.util.UUID;

public record CfpDTO(
        java.util.UUID conferenceId,
        LocalDate cfpOpens,
        LocalDate cfpCloses,
        String conferenceName,
        String conferenceUrl,
        String conferenceDescription,
        EmailAddress contactEmailAddress) {
}
