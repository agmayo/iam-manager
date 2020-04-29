package org.acme.iam.manager.events;

import java.io.FileReader;
import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.jboss.logging.Logger;

import io.vertx.core.json.JsonObject;

@Path("/menu")
public class MenuResource {
    
    private static final Logger LOG = Logger.getLogger(MenuResource.class);
    @ConfigProperty(name = "menu.json")
    String menuJson;

    @GET
    @Counted(name = "menuCalls", description = "How many times the /menu resource has been called")
    @Timed(name = "menuTime", description = "A measure of how long it takes to retrieve a person.", unit = MetricUnits.MILLISECONDS)
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getMenu(){
        LOG.info(menuJson);

        String jsonStr;
		try {

			jsonStr = IOUtils.toString(new FileReader(menuJson));
            JsonObject jsonObj = new JsonObject(jsonStr);
            LOG.info("El json de menu: " + jsonObj);
            return jsonObj;
		} catch (IOException e) {
			// TODO Auto-generated catch block
            e.printStackTrace();
            return null;
		}

    }

}