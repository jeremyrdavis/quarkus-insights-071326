package io.arrogantprogrammer.quarkusinsights.cfp.application;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.aggregates.Presenter;

public class PresenterMapper {

    public static PresenterDTO toDTO(Presenter presenter) {
        if (presenter == null) {
            return null;
        }
        return new PresenterDTO(presenter.getEmail(), presenter.getFirstName(), presenter.getLastName());
    }
}
