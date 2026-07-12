package io.arrogantprogrammer.quarkusinsights.cfp.persistence;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.ConferenceSessionFormat;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.EmailAddress;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.ConferenceTrack;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates.Cfp;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.stream.Collectors;

@ApplicationScoped
public class CfpRepository implements PanacheRepository<CfpEntity> {

    @Transactional
    public Cfp createCfp(Cfp cfp) {
        CfpEntity cfpEntity = toEntity(cfp);
        persist(cfpEntity);
        return toDomain(cfpEntity);
    }
    
    private CfpEntity toEntity(Cfp cfp) {
        return new CfpEntity(
                cfp.getId(),
                cfp.getCfpOpens(),
                cfp.getCfpCloses(),
                cfp.getConferenceName(),
                cfp.getConferenceUrl(),
                cfp.getConferenceDescription(),
                cfp.getConferenceSessionFormats().stream()
                        .map(f -> new FormatEntity(f.formatCode(), f.title(), f.description(), f.duration()))
                        .collect(Collectors.toList()),
                cfp.getTracks().stream()
                        .map(t -> new TrackEntity(t.trackCode(), t.title(), t.description()))
                        .collect(Collectors.toList()),
                cfp.getContactEmailAddress().address()
        );
    }
    
    private Cfp toDomain(CfpEntity cfpEntity) {
        return new Cfp(
                cfpEntity.getId(),
                cfpEntity.getCfpOpens(),
                cfpEntity.getCfpCloses(),
                cfpEntity.getConferenceName(),
                cfpEntity.getConferenceUrl(),
                cfpEntity.getConferenceDescription(),
                cfpEntity.getConferenceSessionFormats().stream()
                        .map(f -> new ConferenceSessionFormat(f.getFormatCode(), f.getTitle(), f.getDescription(), f.getDuration()))
                        .collect(Collectors.toList()),
                cfpEntity.getTracks().stream()
                        .map(t -> new ConferenceTrack(t.getTrackCode(), t.getTitle(), t.getDescription()))
                        .collect(Collectors.toList()),
                new EmailAddress(cfpEntity.getContactEmailAddress())
        );
    }
}
