package io.arrogantprogrammer.quarkusinsights.cfp.application;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.EmailAddress;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates.Presenter;
import io.arrogantprogrammer.quarkusinsights.cfp.persistence.PresenterEntity;

public class PresenterMapper {

    public static PresenterDTO toDTO(Presenter presenter) {
        if (presenter == null) {
            return null;
        }
        return new PresenterDTO(presenter.getId(), presenter.getEmail(), presenter.getFirstName(), presenter.getLastName());
    }

}
