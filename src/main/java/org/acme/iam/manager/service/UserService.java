package org.acme.iam.manager.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.acme.iam.manager.restclient.UserRestClientInterface;
import org.acme.iam.manager.dto.Credential;
import org.acme.iam.manager.dto.IamUser;
import org.acme.iam.manager.dto.UserRegisterRequest;
import org.acme.iam.manager.dto.UserToken;
import org.acme.iam.manager.dto.UserTokenRequest;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

@Named("userService")
@ApplicationScoped
public class UserService {
    private static final Logger LOG = Logger.getLogger(UserService.class);
    
    @Inject
    @RestClient
    UserRestClientInterface UserRestClientInterface;

    @Inject
    TokenService tokenService;
    @Inject
    Validator validator;

    @Transactional
    public List<IamUser> checkUserExistence(String username) {
        UserTokenRequest adminUser= new UserTokenRequest();
        adminUser.setPassword("Pa55w0rd");
        adminUser.setUsername("admin");
        String realm= "master";
        UserToken mytoken = tokenService.buildUserToken(adminUser.getUsername(), adminUser.getPassword(), realm);
        
        List<IamUser> userList = UserRestClientInterface.getUser(realm,
        username,
        "Bearer " + mytoken.getAccessToken(),
        MediaType.APPLICATION_FORM_URLENCODED);
        return userList;

    }
    
    @Transactional
    public Response createUser(UserRegisterRequest userData){
        String username= userData.getUsername();
        String email= userData.getEmail();
        List<Credential> credentials=userData.getCredentials();
        Credential credential= credentials.get(0);
        String type= credential.getType();
        String value= credential.getValue();
        boolean temporary= credential.isTemporary();
        boolean enabled =userData.isEnabled();

        Set<ConstraintViolation<UserRegisterRequest>> violations = validator.validate(userData);

        UserTokenRequest adminUser= new UserTokenRequest();
        adminUser.setPassword("Pa55w0rd");
        adminUser.setUsername("admin");
        String realm ="master";
        UserToken mytoken = tokenService.buildUserToken(adminUser.getUsername(), adminUser.getPassword(), realm);
    
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

        Response iamUser = UserRestClientInterface.createUser(realm,
        "Bearer " + mytoken.getAccessToken(),
        MediaType.APPLICATION_FORM_URLENCODED,
        user);
        return iamUser;
    }
}