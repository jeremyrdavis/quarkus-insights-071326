package io.arrogantprogrammer.quarkusinsights.cfp.domain;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates.ConferenceSession;

import java.time.LocalDate;
import java.util.List;

public record SubmissionContext(LocalDate cfpOpenDate, LocalDate cfpCloseDate, List<ConferenceSessionFormat> formats, List<ConferenceTrack> tracks, List<ConferenceSession> currentSessions) {
}
