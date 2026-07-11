package io.arrogantprogrammer.quarkusinsights.cfp.application;

import io.arrogantprogrammer.quarkusinsights.cfp.domain.CfpAggregate;
import io.arrogantprogrammer.quarkusinsights.cfp.domain.Presenter;
import io.arrogantprogrammer.quarkusinsights.cfp.infrastructure.PresenterParameters;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class CfpService {

    public PresenterDTO registerPresenter(CreatePresenterCommand command){
        Presenter presenter = Presenter.create().withEmail(command.email()).withFirstName(command.firstName()).withLastName(command.lastName());
        return presenter.toDTO();
    }

    public PresenterDTO getPresenter(String email) {
        return null;
    }

    public PresenterDTO updatePresenter(String email, PresenterParameters parameters) {
        return null;
    }

    public void deletePresenter(String email) {

    }

    public CfpDTO createCfp(CreateCfpCommand command) {
        CfpAggregate cfpAggregate = CfpAggregate.create().withCfpOpens(command.cfpOpens()).withCfpCloses(command.cfpCloses());
        CfpDTO cfpDTO = cfpAggregate.toDTO();
        return cfpDTO;
    }
}
