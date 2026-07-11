package io.arrogantprogrammer.quarkusinsights.cfp.infrastructure;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

record CfpParameters(
        @NotNull(message = "CFP opening date is required") LocalDate cfpOpens,
        @NotNull(message = "CFP closing date is required") LocalDate cfpCloses) {
}
