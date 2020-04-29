package org.acme.iam.manager.service;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import javax.validation.Validator;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.acme.iam.manager.dto.Credential;
import org.acme.iam.manager.dto.IamUser;
import org.acme.iam.manager.dto.UserRegisterRequest;
import org.acme.iam.manager.dto.UserToken;
import org.acme.iam.manager.dto.UserTokenRequest;
import org.acme.iam.manager.restclient.UserRestClientInterface;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

@Named("userService")
@ApplicationScoped
public class UserService {
    private static final Logger LOG = Logger.getLogger(UserService.class);

    @ConfigProperty(name = "master.realm")
    String realm;
    @ConfigProperty(name = "admin.password")
    String adminPassword;
    @ConfigProperty(name = "admin.username")
    String adminUsername;
    
    @Inject
    @RestClient
    UserRestClientInterface UserRestClientInterface;

    @Inject
    TokenService tokenService;
    @Inject
    Validator validator;

    @Transactional
    public boolean checkUserExistence(String username) {
        boolean result = false;
        UserToken mytoken= buildAdminToken(adminPassword, adminUsername, realm);
        List<IamUser> userList = UserRestClientInterface.getUser(realm,
        username,
        "Bearer " + mytoken.getAccessToken(),
        MediaType.APPLICATION_FORM_URLENCODED);
        if(userList.size()==0){
            result = false;
        }else{
            result = true;
        }
        return result;

    }
    
    @Transactional
    public boolean createUser(UserRegisterRequest userData){
        boolean result = false;
        String username = userData.getUsername();
        String email = userData.getEmail();
        List<Credential> credentials = userData.getCredentials();
        Credential credential = credentials.get(0);
        String type = credential.getType();
        String value = credential.getValue();
        boolean temporary = credential.isTemporary();
        boolean enabled = userData.isEnabled();

        UserToken mytoken= buildAdminToken(adminPassword, adminUsername, realm);

        Credential cred = new Credential();
        IamUser user = new IamUser();

        cred.setTemporary(temporary);
        cred.setType(type);
        cred.setValue(value);
        ArrayList<Credential> creds= new ArrayList<Credential>();
        creds.add(cred);
        user.setUsername(username);
        user.setEmail(email);
        user.setCredentials(creds);
        user.setEnabled(enabled);

        Response iamResponse = UserRestClientInterface.createUser(realm,
        "Bearer "+ mytoken.getAccessToken(),
        MediaType.APPLICATION_FORM_URLENCODED,
        user);
        if(iamResponse.getStatus() == 201){
            result = true;
        }{
            result = false;
        }
        return result;
    }

    private UserToken buildAdminToken(String adminPassword, String adminUsername,String realm){
        UserTokenRequest adminUser= new UserTokenRequest();
        adminUser.setPassword(adminPassword);
        adminUser.setUsername(adminUsername);
        UserToken mytoken = tokenService.buildUserToken(adminUser.getUsername(), adminUser.getPassword(), realm);
        return mytoken;
    }
}