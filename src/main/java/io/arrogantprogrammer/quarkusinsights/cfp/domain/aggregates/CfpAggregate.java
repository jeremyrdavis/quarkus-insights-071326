package io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates;

import java.time.LocalDate;

public class CfpAggregate {
    
    private LocalDate cfpOpens;

    private LocalDate cfpCloses;

    public static CfpAggregate create() {
        return new CfpAggregate();
    }

    public CfpAggregate withCfpOpens(LocalDate cfpOpens) {
        this.cfpOpens = cfpOpens;
        return this;
    }

    public CfpAggregate withCfpCloses(LocalDate cfpCloses) {
        this.cfpCloses = cfpCloses;
        return this;
    }

    public LocalDate getCfpOpens() {
        return cfpOpens;
    }

    public LocalDate getCfpCloses() {
        return cfpCloses;
    }
}
