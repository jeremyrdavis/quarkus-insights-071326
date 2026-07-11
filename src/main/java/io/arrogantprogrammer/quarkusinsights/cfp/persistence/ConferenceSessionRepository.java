package io.arrogantprogrammer.quarkusinsights.cfp.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ConferenceSessionRepository implements PanacheRepository<ConferenceSessionEntity> {
}
