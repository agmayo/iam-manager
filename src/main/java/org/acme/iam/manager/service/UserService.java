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
import org.acme.iam.manager.exceptions.UserException;
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
    @ConfigProperty(name = "token.authentication")
    String tokenAuthentication;
    
    @Inject
    @RestClient
    UserRestClientInterface UserRestClientInterface;

    @Inject
    TokenService tokenService;
    @Inject
    Validator validator;

    @Transactional
    public List<IamUser> checkUserExistence(String username) throws UserException {

        UserToken mytoken= buildAdminToken(adminPassword, adminUsername, realm);
        List<IamUser> userList = UserRestClientInterface.getUser(realm,
        username,
        tokenAuthentication+ " " + mytoken.getAccessToken(),
        MediaType.APPLICATION_FORM_URLENCODED);
        if(userList.size()==0){
            throw new UserException("User not found");
        }
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

        Response iamUser = UserRestClientInterface.createUser(realm,
        tokenAuthentication+ " " + mytoken.getAccessToken(),
        MediaType.APPLICATION_FORM_URLENCODED,
        user);
        return iamUser;
    }

    private UserToken buildAdminToken(String adminPassword, String adminUsername,String realm){
        UserTokenRequest adminUser= new UserTokenRequest();
        adminUser.setPassword(adminPassword);
        adminUser.setUsername(adminUsername);
        UserToken mytoken = tokenService.buildUserToken(adminUser.getUsername(), adminUser.getPassword(), realm);
        return mytoken;
    }
}