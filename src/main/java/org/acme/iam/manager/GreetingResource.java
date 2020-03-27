// TODO: Leaving it only for reference, should be erased.


package org.acme.iam.manager;

import org.jboss.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;

@Path("/hello")
public class GreetingResource {

    // Logs to be sent to Logstash
    private static final Logger LOG = Logger.getLogger(GreetingResource.class);

    // Metrics to be sent to Logstash
    @GET
    @Counted(name = "helloCalls", description = "How many times the /hello resource has been called")
    @Timed(name = "helloTime", description = "A measure of how long it takes to retrieve a person.", unit = MetricUnits.MILLISECONDS)
    @Produces(MediaType.TEXT_PLAIN)
   
    public String hello() {
        LOG.debug("Request recieved");
        LOG.info("Sending 'hello' response");
        return "hello";
    }
}