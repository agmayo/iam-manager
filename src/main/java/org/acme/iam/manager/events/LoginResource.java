package org.acme.iam.manager.events;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.acme.iam.manager.business.Aggregator;
import org.acme.iam.manager.business.TokenServiceInterface;
import org.acme.iam.manager.dto.TokenData;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

@Path("/login")
public class LoginResource {

    // Logs to be sent to Logstash
    private static final Logger LOG = Logger.getLogger(LoginResource.class);
    @Inject
    @RestClient
    TokenServiceInterface tokenServiceInterface;
    // Metrics to be sent to Prometheus
    @GET
    @Counted(name = "loginCalls", description = "How many times the /login resource has been called")
    @Timed(name = "loginTime", description = "A measure of how long it takes to retrieve a person.", unit = MetricUnits.MILLISECONDS)
    @Produces(MediaType.TEXT_PLAIN)
    public Response login() {
        //TODO: should receive user data to get the propper token.
        String user = "rpradom";
        String password = "rpradom";
        Aggregator aggregator = new Aggregator();
        TokenData data = tokenServiceInterface.getToken("master",
                                                "openid-connect",
                                                "password",
                                                "admin",
                                                "Pa55w0rd",
                                                "admin-cli",
                                                MediaType.APPLICATION_JSON,
                                                MediaType.APPLICATION_FORM_URLENCODED);
        // TODO: We will need some exception management here.
        //String accessToken = aggregator.getAccessToken(user, password);
        String accessToken = data.getAccessToken();
        if(accessToken != null){
            //TODO: access token not being sent!
            LOG.info("Returning token " +  "User: " + user + " Token: " + accessToken);
            return Response.noContent().build();
        }
        else{
            // TODO: Improve with some extra info in the body. 
            // More info: https://stackoverflow.com/questions/26845631/is-it-correct-to-return-404-when-a-rest-resource-is-not-found
            LOG.info("No token could be obtained"+  "User:" + user);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}