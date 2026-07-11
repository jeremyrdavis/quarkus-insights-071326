package io.arrogantprogrammer.quarkusinsights.cfp.application;

import java.time.LocalDate;

public record CreateCfpCommand(LocalDate cfpOpens, LocalDate cfpCloses) {
}
