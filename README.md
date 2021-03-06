# iam-manager

Quarkus Microservice that controls all communication with an Identity Access Manager (A service that manages access to all our resources behind the [gateway](https://www.keycloak.org/documentation))

## Endpoints

This microservice will be an **interface** for the communications with an IAM. 

It is necessary because our [IAM provider](https://access.redhat.com/products/identity-management) might change in the future and if it does we will only need to change this microservice.

### Input

This section explains who is going to access the endpoints.

* `/token` &rarr; This endpoint will receive all the requests that don't include a `JWT Token` and are trying to get one for future secure communications. There can be multiple ways of asking for a token:

  * `/token/raw` &rarr; Simplest way to ask for a token, posting a json: 

    ```bash
    curl --header "Content-Type: application/json" \
      --request POST \
      --data '{"username":"alice","password":"alice"}' \
      http://localhost:8080/token/raw -v
    ```

  * `/token/cookie` &rarr; Ask for the token as a cookie, with a [Basic Auth](https://en.wikipedia.org/wiki/Basic_access_authentication) header.

    ```bash
    curl --user alice:alice \
      --header "Content-Type: application/json" \
      --request POST \
      http://localhost:8080/token/cookie -v
    ```

* `/user/exists/{username}` &rarr; This endpoint will receive all the request that want to verify the existence of a user. Request should include:

  ```bash
  curl --header "Content-Type: application/json" \
    --request GET \
    http://localhost:8080/user/exists/alice -v
  ```

  

* `/user/register` &rarr; This endpoint will receive all the request that want to register a new user in the IAM. Requests should include: 
  
  ```bash
  curl --request POST "http://localhost:8080/user/register" \
  --header "Content-Type: application/json" \
  --data '{"username":"ail","email":"ail@ail.es", "credentials":[{"type":"password","value":"ail","temporary":false}], "enabled":"true"}' -v
  ```

  

- `/roles/{username} `&rarr; This endpoint will receive all the request asking for user roles. Requests should include: 

  ```bash
  curl --header "Content-Type: application/json" \
    --request GET \
    http://localhost:8080/roles/{username} -v
  ```

  

-  `/menu`  &rarr; This endpoint will receive all the request that want to display the menu depending on the role of the user extracted from the token. Requests should include: 

  ```bash
  curl --location --request GET 'http://localhost:8080/menu' \
    -H 'Authorization: Bearer $token'  -v
  ```

  

### Transformation

The requests received will be transformed so they can be understood by our [current IAM provider](https://access.redhat.com/products/identity-management).

* Both `/token` requests get transformed into the following:

  ```bash
  # currently a lot of the parameters are hardcoded.
  # Authorization header: app-authz-rest-springboot:secret
  curl --request POST "http://localhost:9090/auth/realms/master/protocol/openid-connect/token" \
  --header "Content-Type: application/x-www-form-urlencoded" \
  --header 'Authorization: Basic YXBwLWF1dGh6LXJlc3Qtc3ByaW5nYm9vdDpzZWNyZXQ=' \
  -d "client_id=admin-cli" \
  -d "grant_type=password" \
  -d "username=alice" \
  -d "password=alice" -v
  ```

* `/user/exists/{username}` &rarr; This request get transformed into the following:

  ```bash
  curl -X GET \
    http://localhost:9090/auth/admin/realms/master/users\?username\="alice" \
    -H 'Authorization: Bearer $token'  -v
  ```

  

* `/user/register` &rarr; This request get transformed into the following:

  ```bash
  curl "http://localhost:9090/auth/admin/realms/master/users" \
  --header "Content-Type: application/json" \
  --header "Authorization: Bearer $token" \
  --data '{"username":"joe","email":"joe@joe.es", "credentials":[{"type":"password","value":"joe","temporary":false}], "enabled":"true"}' -v
  ```

* ` /roles/{username}` &rarr; This request get transformed into the following:

  ```bash
  curl -k -X GET \
    http://localhost:9090/auth/admin/realms/integration/users/{userid}/role-mappings/realm\
    -H 'Authorization: Bearer $token'  -v
  ```



### Output

All callers should expect a `500 SERVER ERROR` response for internal errors and a `400 BAD REQUEST` for incomplete requests. If there are no errors, the following responses are expected:

* Both `/token` requests return a valid token with slight variations:

  * `/token/raw` &rarr; Returns an *access token* and a *refresh token*, each one with it's expiration time.

    ```json
    {
        "accessToken": "eyJh...xXqxmXnI8fzWusM",
        "accessTokenExpiration": 300,
        "refreshToken": "eyJhbGciOi..._MzB2COBdb0U",
        "refreshTokenExpiration": 1800
    }
    ```

  * `/token/cookie` &rarr; Returns two cookies, One contains the token's payload and header and the other one the signature. This endpoint is for authentication from a browser. Get more info [here](https://medium.com/lightrail/getting-token-authentication-right-in-a-stateless-single-page-application-57d0c6474e3).

    ```
    < HTTP/1.1 204 No Content
    * Added cookie payload="eyJhbGciOiJSUzI1...Y2UifQ" for domain localhost, path /, expire 1587394916
    < Set-Cookie: payload=eyJhbGciOiJSUzI1Ni...XJuYW1lIjoiYWxpY2UifQ;Version=1;Domain=localhost;Path=/;Max-Age=299
    * Added cookie signature="RpcMXsZM...Y3Qddg4qQ" for domain localhost, path /, expire 1587394916
    < Set-Cookie: signature=RpcMXsZMqyuhy...QxQxXY3Qddg4qQ;Version=1;Domain=localhost;Path=/;Max-Age=299;HttpOnly
    < 
    ```

* `/user/exists/{username}` &rarr; Returns the representation of the user

  ```
  {
     "id":"56b55ded-fa60-4d97-b04b-b1579d7d2af7",
     "createdTimestamp":1585742824508,
     "username":"alice",
     "enabled":true,
     "totp":false,
     "emailVerified":false,
     "firstName":"Alice",
     "lastName":"Alice",
     "email":"alice@alice.com",
     "disableableCredentialTypes":[],
     "requiredActions":[],
     "notBefore":0,
     "access":{
        "manageGroupMembership":true,
        "view":true,
        "mapRoles":true,
        "impersonate":true,
        "manage":true
     }
  }
  ```

  

* `/user/register` &rarr; If the user was correctly created it returns:

  ```bash
  *   Trying ::1:8080...
  * TCP_NODELAY set
  * Connected to localhost (::1) port 8080 (#0)
  > POST /user/register HTTP/1.1
  > Host: localhost:8080
  > User-Agent: curl/7.66.0
  > Accept: */*
  > Content-Type: application/json
  > Content-Length: 126
  > 
  * upload completely sent off: 126 out of 126 bytes
  * Mark bundle as not supporting multiuse
  < HTTP/1.1 204 No Content
  < 
  * Connection #0 to host localhost left intact
  ```

  ​	If the username is missing it returns:

  ```bash
  *   Trying ::1:8080...
  * TCP_NODELAY set
  * Connected to localhost (::1) port 8080 (#0)
  > POST /user/register HTTP/1.1
  > Host: localhost:8080
  > User-Agent: curl/7.66.0
  > Accept: */*
  > Content-Type: application/json
  > Content-Length: 109
  > 
  * upload completely sent off: 109 out of 109 bytes
  * Mark bundle as not supporting multiuse
  < HTTP/1.1 400 Bad Request
  < Content-Length: 215
  < validation-exception: true
  < Content-Type: application/json
  < 
  * Connection #0 to host localhost left intact
  {"classViolations":[],"parameterViolations":[{"constraintType":"PARAMETER","message":"Username must not be null","path":"createUser.userData.username","value":""}],"propertyViolations":[],"returnValueViolations":[]}%  
  ```

- `/menu ` &rarr; Returns the json with the menu: 

  ```bash
  {"access_token":"eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ5NEt2dGE3U29yYzBvdTAta0MzQXpLMXptalZRckhZLWxXSU9hdjk2aUhFIn0.eyJleHAiOjE1ODkxOTY3OTEsImlhdCI6MTU4OTE5NjQ5MSwianRpIjoiNjFhZDU4ZWYtNGFlMi00Y2M4LWI3NmMtOTA2OTI4NmU5N2NmIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo5MDkwL2F1dGgvcmVhbG1zL2ludGVncmF0aW9uIiwic3ViIjoiNWJmZWQ1OTMtOTgxMS00ZTM4LWI0M2UtNTZlZGIzOWEwZDMxIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYWRtaW4tY2xpIiwic2Vzc2lvbl9zdGF0ZSI6IjlmODZkMGJmLWE4ZWQtNDRlNy1hMmQzLTgzN2M5MzgwZGI0ZSIsImFjciI6IjEiLCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsInByZWZlcnJlZF91c2VybmFtZSI6ImNoYXJsaWUiLCJlbWFpbCI6ImNoYXJsaWVAaW50ZWdyYXRpb24uY29tIn0.JAU-hgdFqIn1qOeKeS3Xsi87lnVGY5PZ-RxkrwpjAR2Kk53vI4EMp4T_QYv6Es8NvCGSjLmcWD52Fh-2v7D0DNfyo8pOjW6Q6LX_3L8kbS-NLQj11-9kMp_AaO_mpUxeyGpcd795NR8wxb1lgicvKsVEwUVxhRIvPMsGl4WZNeSsrQ9og-0uAdGKVXcQ3axMqwfckO2ToQUCdx0WQ4YiszxS1ACk2-1YBNEwYZSfIEEVFZRaTeOUgpVD8NLYOpevmSukCX98b5IY0xcXhc9ybuxC7JC3GLPZBDfkDpJlrQJOhVg6tCg3njduXygIYKltrwAfhIZ9SedzxQUhTVsJ5g","expires_in":300,"refresh_expires_in":1800,"refresh_token":"eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI4NjQ2OGZmOS1mZmVhLTQ0MmQtOWZkNy0zNTE5M2Y4NGEwZjcifQ.eyJleHAiOjE1ODkxOTgyOTEsImlhdCI6MTU4OTE5NjQ5MSwianRpIjoiZGM1ZWVjMTktYjlhNC00NTc4LTg5OTQtYjUxYTllNjYxOWExIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo5MDkwL2F1dGgvcmVhbG1zL2ludGVncmF0aW9uIiwiYXVkIjoiaHR0cDovL2xvY2FsaG9zdDo5MDkwL2F1dGgvcmVhbG1zL2ludGVncmF0aW9uIiwic3ViIjoiNWJmZWQ1OTMtOTgxMS00ZTM4LWI0M2UtNTZlZGIzOWEwZDMxIiwidHlwIjoiUmVmcmVzaCIsImF6cCI6ImFkbWluLWNsaSIsInNlc3Npb25fc3RhdGUiOiI5Zjg2ZDBiZi1hOGVkLTQ0ZTctYTJkMy04MzdjOTM4MGRiNGUiLCJzY29wZSI6InByb2ZpbGUgZW1haWwifQ.VAyJwa8BV9O6an-D87jC6Y-iNMuhEfkQ_3hyF7bdnQg","token_type":"bearer","not-before-policy":0,"session_state":"9f86d0bf-a8ed-44e7-a2d3-837c9380db4e","scope":"profile email"}
  ```

- `/roles/{username}` &rarr; It returns:

  ```bash
  > GET /roles/charlie HTTP/1.1
  > Host: localhost:8080
  > User-Agent: curl/7.66.0
  > Accept: */*
  > Content-Type: application/json
  > 
  * Mark bundle as not supporting multiuse
  < HTTP/1.1 204 No Content
  ```

  


## Health check

We created a simple liveness health check procedure which states whether our application is running or not and a readiness health check which will be able to state whether our application is able to process requests.

Health check procedures are defined as implementations of the `HealthCheck` interface which are defined as CDI beans with the CDI qualifier `@Liveness`. If you run the health check at http://localhost:8080/health/live the checks array will contain only the  check defined with the `@Liveness` qualifier.

We will create another health check procedure that accesses our IAM. If the IAM can be accessed, then we will always return the response indicating it is ready. 

If you access http://localhost:8080/health/ready you will see only the IAM connection health check as it is the only health check defined with the `@Readiness` qualifier. 

If you access http://localhost:8080/health you will get back both checks.

## Docker

Information about docker and iam-manager integration can be found at `/doc/docker-info.md`.

## More info

* [User management API docs](https://www.keycloak.org/docs-api/9.0/rest-api/index.html#_users_resource)
* [User existance check](https://stackoverflow.com/questions/52726048/keycloak-how-to-check-if-username-and-email-exists)
* [User creation](https://stackoverflow.com/questions/52440546/create-user-on-keycloack-from-curl-command)
* [curl example of user creation](https://issues.redhat.com/browse/KEYCLOAK-5383)

curl -v http://localhost:9090/auth/admin/realms/master/users?username=alice&email=alice@alice.com