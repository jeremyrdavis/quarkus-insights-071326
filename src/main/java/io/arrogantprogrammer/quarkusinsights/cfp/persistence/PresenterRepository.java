package io.arrogantprogrammer.quarkusinsights.cfp.persistence;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.EmailAddress;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates.Presenter;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.Optional;

@ApplicationScoped
public class PresenterRepository implements PanacheRepository<PresenterEntity> {

    public Optional<PresenterEntity> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    @Transactional
    public Presenter register(Presenter presenter) {
        PresenterEntity presenterEntity = toEntity(presenter);
        persist(presenterEntity);
        return toDomain(presenterEntity);
    }

    private PresenterEntity toEntity(Presenter presenter) {
        return new PresenterEntity(
                presenter.getId(),
                presenter.getEmail().address(),
                presenter.getFirstName(),
                presenter.getLastName()
        );
    }

    public Presenter toDomain(PresenterEntity presenterEntity) {
        return new Presenter(
                presenterEntity.getId(),
                new EmailAddress(presenterEntity.getEmail()),
                presenterEntity.getFirstName(),
                presenterEntity.getLastName()
        );
    }
}
