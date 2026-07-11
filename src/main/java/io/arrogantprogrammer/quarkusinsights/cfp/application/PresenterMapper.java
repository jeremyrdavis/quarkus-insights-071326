package io.arrogantprogrammer.quarkusinsights.cfp.application;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.EmailAddress;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates.Presenter;
import io.arrogantprogrammer.quarkusinsights.cfp.persistence.PresenterEntity;

public class PresenterMapper {

    public static PresenterDTO toDTO(Presenter presenter) {
        if (presenter == null) {
            return null;
        }
        return new PresenterDTO(presenter.getEmail(), presenter.getFirstName(), presenter.getLastName());
    }

    public static PresenterEntity toEntity(Presenter presenter) {
        if (presenter == null) {
            return null;
        }
        return new PresenterEntity(presenter.getEmail().address(), presenter.getFirstName(), presenter.getLastName());
    }

    public static Presenter toDomain(PresenterEntity presenterEntity) {
        if (presenterEntity == null) {
            return null;
        }
        return Presenter.create(
                new EmailAddress(presenterEntity.getEmail()),
                presenterEntity.getFirstName(),
                presenterEntity.getLastName());
    }
}
