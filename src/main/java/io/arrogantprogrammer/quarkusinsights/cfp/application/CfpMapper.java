package io.arrogantprogrammer.quarkusinsights.cfp.application;
    
import io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates.Cfp;
import io.arrogantprogrammer.quarkusinsights.cfp.persistence.CfpEntity;
import io.arrogantprogrammer.quarkusinsights.cfp.persistence.FormatEntity;
import io.arrogantprogrammer.quarkusinsights.cfp.persistence.TrackEntity;

import java.util.stream.Collectors;

public class CfpMapper {

    public static CfpDTO toDTO(Cfp cfp) {
        if (cfp == null) {
            return null;
        }
        return new CfpDTO(
                cfp.getId(),
                cfp.getCfpOpens(),
                cfp.getCfpCloses(),
                cfp.getConferenceName(),
                cfp.getConferenceUrl(),
                cfp.getConferenceDescription(),
                cfp.getTracks(),
                cfp.getConferenceSessionFormats(),
                cfp.getContactEmailAddress());
    }

}
