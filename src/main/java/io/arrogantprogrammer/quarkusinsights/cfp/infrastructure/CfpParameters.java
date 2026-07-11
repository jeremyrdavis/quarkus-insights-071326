package io.arrogantprogrammer.quarkusinsights.cfp.infrastructure;

import java.time.LocalDate;

record CfpParameters(LocalDate cfpOpens, LocalDate cfpCloses) {
}
