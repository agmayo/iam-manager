package org.acme.iam.manager.events;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import java.util.List;

import org.acme.iam.manager.service.RolesService;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.jaxrs.PathParam;


@Path("/roles")
public class RolesResource {

    // Logs to be sent to Logstash
    private static final Logger LOG = Logger.getLogger(UserResource.class);

    @Inject
    RolesService roleService;

    @GET
    @Counted(name = "rolesCalls", description = "How many times the /roles resource has been called")
    @Timed(name = "rolesTime", description = "A measure of how long it takes to retrieve a person.", unit = MetricUnits.MILLISECONDS)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{username}")
    public Response getUserRoles(@PathParam String username) {

        try {
            List<String> userRoles = roleService.getUserRoles(username);
            if(userRoles.isEmpty()==false){
                LOG.info("userRoles:"+ userRoles); 
                return Response.noContent().build();
            }else{
                LOG.error("Roles could not be found");
                return Response.status(Response.Status.NOT_FOUND).build();
            }

        }catch(WebApplicationException wae){
            LOG.info("Roles could not be found");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }  
        
    }
}