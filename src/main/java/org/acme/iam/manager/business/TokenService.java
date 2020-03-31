package org.acme.iam.manager.business;

import static io.restassured.RestAssured.given;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.restassured.http.ContentType;
import io.restassured.response.Response;


@Path("http://localhost:9090/auth")
@RegisterRestClient

public class TokenService {

    @POST
    @Path("/realms/{realm}/protocol/{protocol}/token")
    @Produces(MediaType.APPLICATION_JSON)
    public static String getAdminToken(){
        String secret ="secret";

        Response response = given().urlEncodingEnabled(
                true)
        .auth().preemptive().basic("app-authz-rest-springboot", secret)
        .param("grant_type", "password")
        .param("client_id", "admin-cli")
        .param("username", "admin")
        .param("password", "Pa55w0rd")
        .header("Accept", ContentType.JSON.getAcceptHeader())
        .post("http://localhost:9090/auth/realms/master/protocol/openid-connect/token")
        .then().statusCode(200).extract()
        .response();
    JsonReader jsonReader = Json.createReader(new StringReader(response.getBody().asString()));
  
    JsonObject object =  jsonReader.readObject();
    jsonReader = Json.createReader(new StringReader(response.getBody().asString()));
    object = jsonReader.readObject();
    String adminToken = object.getString("access_token");
    return adminToken;
    }
    @POST
    @Path("/realms/{realm}/protocol/{protocol}/token")
    @Produces(MediaType.APPLICATION_JSON)
    public static String getUserToken(){
        String secret ="secret";

        Response response = given().urlEncodingEnabled(
                true)
        .auth().preemptive().basic("app-authz-rest-springboot", secret)
        .param("grant_type", "password")
        .param("client_id", "admin-cli")
        .param("username", "admin")
        .param("password", "Pa55w0rd")
        .header("Accept", ContentType.JSON.getAcceptHeader())
        .post("http://localhost:9090/auth/realms/master/protocol/openid-connect/token")
        .then().statusCode(200).extract()
        .response();
    JsonReader jsonReader = Json.createReader(new StringReader(response.getBody().asString()));
  
    JsonObject object =  jsonReader.readObject();
    jsonReader = Json.createReader(new StringReader(response.getBody().asString()));
    object = jsonReader.readObject();
    String adminToken = object.getString("access_token");
    return adminToken;
    }

}