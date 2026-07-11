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
                cfp.getContactEmailAddress());
    }

    public static CfpEntity toEntity(Cfp cfp) {
        if (cfp == null) {
            return null;
        }
        return new CfpEntity(
                cfp.getCfpOpens(),
                cfp.getCfpCloses(),
                cfp.getConferenceName(),
                cfp.getConferenceUrl(),
                cfp.getConferenceDescription(),
                cfp.getConferenceSessionFormats().stream().map(format -> new FormatEntity(format.formatCode(), format.title(), format.description(), format.duration())).collect(Collectors.toList()),
                cfp.getTracks().stream().map(track -> new TrackEntity(track.trackCode(), track.title(), track.description())).collect(Collectors.toList()),
                cfp.getContactEmailAddress().address());
    }
}
