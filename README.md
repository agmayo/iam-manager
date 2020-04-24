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




## Health check

We created a simple liveness health check procedure which states whether our application is running or not and a readiness health check which will be able to state whether our application is able to process requests.

Health check procedures are defined as implementations of the `HealthCheck` interface which are defined as CDI beans with the CDI qualifier `@Liveness`. If you run the health check at http://localhost:8080/health/live the checks array will contain only the  check defined with the `@Liveness` qualifier.

We will create another health check procedure that accesses our IAM. If the IAM can be accessed, then we will always return the response indicating it is ready. 

If you access http://localhost:8080/health/ready you will see only the IAM connection health check as it is the only health check defined with the `@Readiness` qualifier. 

If you access http://localhost:8080/health you will get back both checks.

## Docker

This application is fully ready to be used with docker. You can find all the files in `src/main/docker`

### Deploy

#### Dev environment

If you want to create code in your local machine you can execute: 

```bash
# In the /src/main/docker dir:
docker-compose -f docker-compose.yml -f overrides/docker-compose.local-dev.yml up -d
```

#### Prod environment from code

If you want to containerize your application and test it in a docker network you can execute:

```bash
# In the /src/main/docker dir:
docker-compose -f docker-compose.yml -f overrides/docker-compose.build-prod.yml up -d
```

#### Prod environment from images

If you want to generate all the containers from the images in the docker registry:

``` bash
# In the /src/main/docker dir:
docker-compose -f docker-compose.yml -f overrides/docker-compose.download-prod.yml up -d
```

### Upload

1. Generate the artifact:

   * For the native image:

     ```bash
     # In the base of the project
     mvn package -Pnative
     ```

   * For the JVM image:

     ```bash
     # In the base of the project
     mvn package
     ```

2. Generate the docker images:

   * For the native image:

     ```bash
     # In the base of the project
     docker build -f src/main/docker/Dockerfile.native -t iam_manager:native . 
     ```

   * For the JVM image:

     ```bash
     # In the base of the project
     docker build -f src/main/docker/Dockerfile.jvm -t iam_manager:jvm . 
     ```

3. Retag the docker images for the repository:

   * For the native image:

     ```bash
     # For a private docker registry:
     # docker tag iam_manager:jvm <registry_url>/<your_organization>/iam_manager:native
     
     # For the docker hub:
     docker tag iam_manager:native rpardom/iam_manager:native
     ```

   * For the JVM machine:

     ```bash
     # For a private docker registry:
     # docker tag iam_manager:jvm <registry_url>/<your_organization>/iam_manager:jvm
     
     # For the docker hub:
     docker tag iam_manager:jvm rpardom/iam_manager:jvm
     ```

4. Push the tagged images:

   ```bash
   # For a private docker registry:
   # docker login <registry_url>
   # docker push <registry_url>/<your_organization>/iam_manager
   
   # For the docker hub:
   docker login
   docker push rpardom/iam_manager
   ```

## More info

* [User management API docs](https://www.keycloak.org/docs-api/9.0/rest-api/index.html#_users_resource)
* [User existance check](https://stackoverflow.com/questions/52726048/keycloak-how-to-check-if-username-and-email-exists)
* [User creation](https://stackoverflow.com/questions/52440546/create-user-on-keycloack-from-curl-command)
* [curl example of user creation](https://issues.redhat.com/browse/KEYCLOAK-5383)

curl -v http://localhost:9090/auth/admin/realms/master/users?username=alice&email=alice@alice.com