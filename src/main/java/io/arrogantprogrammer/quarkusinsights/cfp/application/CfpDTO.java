package io.arrogantprogrammer.quarkusinsights.cfp.application;

import java.time.LocalDate;

public record CfpDTO(LocalDate cfpOpens, LocalDate cfpCloses) {}
