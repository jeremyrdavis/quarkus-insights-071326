package io.arrogantprogrammer.quarkusinsights.cfp.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class PresenterRepository implements PanacheRepository<PresenterEntity> {

    public Optional<PresenterEntity> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }
}
