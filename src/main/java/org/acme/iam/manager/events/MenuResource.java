package org.acme.iam.manager.events;

import java.io.FileReader;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.acme.iam.manager.service.RolesService;
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
    @ConfigProperty(name = "admin.menu.json")
    String adminMenu;
    @ConfigProperty(name = "user.menu.json")
    String userMenu;
    
    JsonObject def;

    @Inject
    RolesService roleService;

    @GET
    @Counted(name = "menuCalls", description = "How many times the /menu resource has been called")
    @Timed(name = "menuTime", description = "A measure of how long it takes to retrieve a person.", unit = MetricUnits.MILLISECONDS)
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject getMenu(@HeaderParam("Authorization") String authorizationHeader){

        LOG.info(adminMenu);
        String token = authorizationHeader.split(" ")[1];

        //token="eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJiMDlMaU9mU2pzbWlNZEtzLURvbm9valZQRlpudUZEb3Z1V21IZERmR1VBIn0.eyJleHAiOjE1ODg1ODg2NDUsImlhdCI6MTU4ODU4ODM0NSwianRpIjoiYTgxYmJjZjMtOTMwMy00Y2E0LWI4Y2ItMmQyNTFkMmIxZGU1IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo5MDkwL2F1dGgvcmVhbG1zL2ludGVncmF0aW9uIiwic3ViIjoiMTI1ZjQ1YjMtMDVjZS00ZjE0LWE1YjYtZTA1MThkYjdmMmU5IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYWRtaW4tY2xpIiwic2Vzc2lvbl9zdGF0ZSI6IjJmNDlmZDM5LWIxNDUtNGEyOC1iZTljLTBjNjAzZTg2M2UzYiIsImFjciI6IjEiLCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsInByZWZlcnJlZF91c2VybmFtZSI6ImNoYXJsaWUifQ.gABx6OEofEdYKmLqJQ0mvrePQtxNRuzE_Oymh3AvgAwPvUmhA7_bqEdCv80ueFFU4uEi_lv5OCmPxhqO7bUwXzKFrgTSmzUB_sEEZGa-kZSgYVh8FZguNHcAscMebGN_MoLZgm1wSFHf-oBXDgE0bepIaniLPurfuj7Na6kfG_GneNLO34KCxt0vukJau0G5NFZDomX3CCaV2hEAfxxlrGUL927FNNIlGXDCXGsau9F6oEgNUTRCu0gPy9pN3LN-981scc8dvTQgWc_d0k9nUNGhoeqeZXV7DRrfSMaVVUi7CHxVSu14KzfMa0UUJcq95ZV3hSZonWgkJh2r1Q0Kjg";
        String[] tokenParts = token.split("[.]");
        String tokenPayload = tokenParts[1];
        byte[] decoded = Base64.getDecoder().decode(tokenPayload);
        String decodedString = new String(decoded);
        JsonObject jsonOb = new JsonObject(decodedString.toString());
        String username = jsonOb.getString("preferred_username");
        LOG.info("El nombre de usuario es: " + username);
        List<String> userRoles = roleService.getUserRoles(username);
        LOG.info("Los roles del usuario son: " + userRoles);


        String adminStr;
        String userStr;
		try {
            
            userStr = IOUtils.toString(new FileReader(userMenu));
            JsonObject jsonObjU = new JsonObject(userStr);
            //LOG.info("El json de menu: " + jsonObjU);
			adminStr = IOUtils.toString(new FileReader(adminMenu));
            JsonObject jsonObjA = new JsonObject(adminStr);
            //LOG.info("El json de menu: " + jsonObjA);
            for (int i =0;i<userRoles.size();i++){
                if(userRoles.get(i).equals("SuperAdmin")){
                    def= jsonObjA;
                    break;
                }else{
                    def= jsonObjU;
                }
            }

            return def;
            
		} catch (IOException e) {
			// TODO Auto-generated catch block
            e.printStackTrace();
            return null;
		}

    }

}