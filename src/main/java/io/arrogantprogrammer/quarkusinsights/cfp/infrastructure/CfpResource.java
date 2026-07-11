package io.arrogantprogrammer.quarkusinsights.cfp.infrastructure;

import io.arrogantprogrammer.quarkusinsights.cfp.application.CfpService;
import io.arrogantprogrammer.quarkusinsights.cfp.application.CreateCfpCommand;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/cfp")
public class CfpResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(CfpResource.class);

    @Inject
    CfpService cfpService;

    @POST
    public Response createCfp(@Valid CfpParameters parameters){
        LOGGER.debug("createCfp: {}", parameters);
        CreateCfpCommand createCfpCommand = new CreateCfpCommand(
                parameters.cfpOpens(),
                parameters.cfpCloses()
        );
        var cfpDTO = cfpService.createCfp(createCfpCommand);
        return Response.ok().entity(cfpDTO).build();
    }
}
