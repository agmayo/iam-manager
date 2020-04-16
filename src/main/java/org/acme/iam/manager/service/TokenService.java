package org.acme.iam.manager.service;

import java.util.Base64;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.acme.iam.manager.restclient.TokenRestClientInterface;
import org.acme.iam.manager.dto.TokenData;
import org.acme.iam.manager.dto.UserToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

@Named("tokenService")
@ApplicationScoped
public class TokenService {
    private static final Logger LOG = Logger.getLogger(TokenService.class);
    
    @Inject
    @RestClient
    TokenRestClientInterface tokenServiceInterface;

    @Transactional
    public UserToken buildUserToken(String user, String password, String realm)  throws WebApplicationException{
        UserToken tokenData = new UserToken();
        String authorizationHeader = "Basic " + Base64.getEncoder().encodeToString("app-authz-rest-springboot:secret".getBytes());
        TokenData iamTokenInfo = tokenServiceInterface.getToken(realm,
        "openid-connect",
        "password",
        user,
        password,
        "admin-cli",
        MediaType.APPLICATION_JSON,
        MediaType.APPLICATION_FORM_URLENCODED,
        authorizationHeader);
        
        tokenData.setAccessToken(iamTokenInfo.getAccessToken());
        tokenData.setRefreshToken(iamTokenInfo.getRefreshToken());
        tokenData.setAccessTokenExpiration(iamTokenInfo.getExpiresIn());
        
        tokenData.setRefreshTokenExpiration(iamTokenInfo.getRefreshExpiration());
        return tokenData;
    }
}