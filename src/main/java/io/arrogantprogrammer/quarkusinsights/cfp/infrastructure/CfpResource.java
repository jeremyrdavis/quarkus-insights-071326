package io.arrogantprogrammer.quarkusinsights.cfp.infrastructure;

import io.arrogantprogrammer.quarkusinsights.cfp.application.CfpService;
import io.arrogantprogrammer.quarkusinsights.cfp.application.CreateCfpCommand;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/cfp")
public class CfpResource {

    @Inject
    CfpService cfpService;

    @POST
    public Response createCfp(CfpParameters parameters){
        CreateCfpCommand createCfpCommand = new CreateCfpCommand(
                parameters.cfpOpens(),
                parameters.cfpCloses()
        );
        var cfpDTO = cfpService.createCfp(createCfpCommand);
        return Response.ok().entity(cfpDTO).build();
    }
}
