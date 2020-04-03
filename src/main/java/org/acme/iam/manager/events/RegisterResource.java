package org.acme.iam.manager.events;

import org.jboss.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.acme.iam.manager.business.Aggregator;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;

@Path("/register")
public class RegisterResource {

    // Logs to be sent to Logstash
    private static final Logger LOG = Logger.getLogger(RegisterResource.class);

    // Metrics to be sent to Prometheus
    @GET
    @Counted(name = "existsCalls", description = "How many times the /register/exists resource has been called")
    @Timed(name = "existsTime", description = "A measure of how long it takes to retrieve a person.", unit = MetricUnits.MILLISECONDS)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/exists")
    public Response checkUserExistence() {
        //TODO: should receive user data to check its existence.
        Aggregator aggregator = new Aggregator();
        Boolean userExists = aggregator.checkUserExistence();
        // TODO: We will need some exception management here.
        if(userExists){
            //LOG.info("User exists, returning 204");
            return Response.noContent().build();
        }
        else{
            // TODO: Improve with some extra info in the body. 
            // More info: https://stackoverflow.com/questions/26845631/is-it-correct-to-return-404-when-a-rest-resource-is-not-found
            //LOG.info("User could not be found, returning 404");
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}