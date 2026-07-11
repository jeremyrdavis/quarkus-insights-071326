package io.arrogantprogrammer.quarkusinsights.cfp.application;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates.CfpAggregate;

public class CfpMapper {

    public static CfpDTO toDTO(CfpAggregate cfpAggregate) {
        if (cfpAggregate == null) {
            return null;
        }
        return new CfpDTO(cfpAggregate.getCfpOpens(), cfpAggregate.getCfpCloses());
    }
}
