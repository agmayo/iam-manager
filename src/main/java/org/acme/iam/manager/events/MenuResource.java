package org.acme.iam.manager.events;

import java.io.FileNotFoundException;
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
        if(authorizationHeader !=null){
            String username = getUsername(authorizationHeader);
            List<String> userRoles = roleService.getUserRoles(username);
            LOG.info("The roles of the user whose username is " +username +" are: " +userRoles);
            
            String adminStr;
            String userStr;
            try {
                
                userStr = IOUtils.toString(new FileReader(userMenu));
                JsonObject jsonObjU = new JsonObject(userStr);
                adminStr = IOUtils.toString(new FileReader(adminMenu));
                JsonObject jsonObjA = new JsonObject(adminStr);
                if(userRoles.contains("SuperAdmin")){
                    def= jsonObjA;
                }else if(userRoles.contains("BorderAdmin")){
                    def= jsonObjA;
                }else if(userRoles.contains("BasicUser")){
                    def= jsonObjU;
                }else if(userRoles.contains("Tester")){
                    def= jsonObjU;
                }
                else{
                    LOG.error("Role not exist");
                    def= null;
                }
             
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                LOG.error("Menu not found");
            } catch(IOException e){
                e.printStackTrace();
            } 
        
            return def;
        }
        else{
            LOG.warn("The Authorization header is missing.");
            try{
                String userStr = IOUtils.toString(new FileReader(userMenu));
                JsonObject jsonObjU = new JsonObject(userStr);
                def=jsonObjU;
            }catch (FileNotFoundException e) {
                e.printStackTrace();
                LOG.error("Menu not found");
            } catch(IOException e){
                e.printStackTrace();
            } 
            return def;

        }
     

    }

    private String getUsername(String authorizationHeader){
        String token = authorizationHeader.split(" ")[1];
        //signature is not being checked
        String[] tokenParts = token.split("[.]");
        String tokenPayload = tokenParts[1];
        byte[] decoded = Base64.getDecoder().decode(tokenPayload);
        String decodedString = new String(decoded);
        JsonObject jsonOb = new JsonObject(decodedString.toString());
        String username = jsonOb.getString("preferred_username");
        return username;
        
    }

}