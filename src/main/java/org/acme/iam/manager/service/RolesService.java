package org.acme.iam.manager.service;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import javax.ws.rs.core.MediaType;

import org.acme.iam.manager.dto.IamUser;
import org.acme.iam.manager.dto.RealmMappings;
import org.acme.iam.manager.dto.UserToken;
import org.acme.iam.manager.dto.UserTokenRequest;
import org.acme.iam.manager.restclient.UserRestClientInterface;
import org.acme.iam.manager.restclient.UserRoleRestClientInterface;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import io.vertx.core.json.JsonObject;


@Named("rolesService")
@ApplicationScoped
public class RolesService {

    private static final Logger LOG = Logger.getLogger(RolesService.class);
    List<String> list = new ArrayList<String>();

    @ConfigProperty(name = "master.realm")
    String realm;
    @ConfigProperty(name = "admin.password")
    String adminPassword;
    @ConfigProperty(name = "admin.username")
    String adminUsername;
   
    @Inject
    @RestClient
    UserRoleRestClientInterface UserRoleRestClientInterface;

    @Inject
    @RestClient
    UserRestClientInterface UserRestClientInterface;

    @Inject
    TokenService tokenService;
    @Inject
    UserService userService;

    @Transactional
    public List<String> getUserRoles(String username) {
        List<String> list = new ArrayList<String>();
        UserToken mytoken= buildAdminToken(adminPassword, adminUsername, realm);
        List<IamUser> userList = UserRestClientInterface.getUser(realm,
        username,
        "Bearer " + mytoken.getAccessToken(),
        MediaType.APPLICATION_FORM_URLENCODED);
        IamUser user = userList.get(0);
        String user_id = user.getId();
        
        List<RealmMappings> roles = UserRoleRestClientInterface.getUserRoles(realm,
        user_id,
        "Bearer " + mytoken.getAccessToken(),
        MediaType.APPLICATION_FORM_URLENCODED);
       
        for (int i = 0; i<roles.size(); i++){
            list.add(roles.get(i).getName());
        }

        return list; 

    }
    private UserToken buildAdminToken(String adminPassword, String adminUsername,String realm){
        UserTokenRequest adminUser= new UserTokenRequest();
        adminUser.setPassword(adminPassword);
        adminUser.setUsername(adminUsername);
        UserToken mytoken = tokenService.buildUserToken(adminUser.getUsername(), adminUser.getPassword(), realm);
        return mytoken;
    }

}