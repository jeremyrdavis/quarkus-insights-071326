package io.arrogantprogrammer.quarkusinsights.cfp.persistence;

import io.arrogantprogrammer.quarkusinsights.cfp.application.UpdateCfpCommand;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.ConferenceSessionFormat;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.EmailAddress;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.ConferenceTrack;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.FormatCode;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates.Cfp;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class CfpRepository implements PanacheRepository<CfpEntity> {

    @Transactional
    public Cfp createCfp(Cfp cfp) {
        CfpEntity cfpEntity = toEntity(cfp);
        persist(cfpEntity);
        return toDomain(cfpEntity);
    }

    public Optional<Cfp> findByUUID(UUID cfpId) {
        return Optional.ofNullable(find("id", cfpId).<CfpEntity>firstResult()).map(this::toDomain);
    }

    public List<Cfp> findAllCfps() {
        return listAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Transactional
    public Optional<Cfp> updateCfp(UUID cfpId, UpdateCfpCommand command) {
        CfpEntity entity = find("id", cfpId).firstResult();
        if (entity == null) {
            return Optional.empty();
        }
        entity.setCfpOpens(command.cfpOpens());
        entity.setCfpCloses(command.cfpCloses());
        entity.setConferenceName(command.conferenceName());
        entity.setConferenceUrl(command.conferenceUrl());
        entity.setConferenceDescription(command.conferenceDescription());
        entity.setContactEmailAddress(command.contactEmailAddress().address());
        Log.debugf("Updated CFP with id %s", cfpId);
        return Optional.of(toDomain(entity));
    }

    @Transactional
    public boolean deleteCfp(UUID cfpId) {
        CfpEntity entity = find("id", cfpId).<CfpEntity>firstResult();
        if (entity == null) {
            return false;
        }
        getEntityManager().remove(entity);
        return true;
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
                        .map(f -> new FormatEntity(f.formatCode().name(), f.title(), f.description(), f.duration()))
                        .collect(Collectors.toList()),
                cfp.getTracks().stream()
                        .map(t -> new TrackEntity(t.trackCode().toString(), t.title(), t.description()))
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
                        .map(f -> new ConferenceSessionFormat(FormatCode.valueOf(f.getFormatCode()), f.getTitle(), f.getDescription(), f.getDuration()))
                        .collect(Collectors.toList()),
                cfpEntity.getTracks().stream()
                        .map(t -> new ConferenceTrack(t.getTrackCode().toString(), t.getTitle(), t.getDescription()))
                        .collect(Collectors.toList()),
                new EmailAddress(cfpEntity.getContactEmailAddress())
        );
    }

}
