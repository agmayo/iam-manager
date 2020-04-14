# iam-manager

Quarkus Microservice that controls all communication with an Identity Access Manager (A service that manages access to all our resources behind the [gateway](https://www.keycloak.org/documentation))

## Endpoints

This microservice will be an **interface** for the communications with an IAM. 

It is necessary because our [IAM provider](https://access.redhat.com/products/identity-management) might change in the future and if it does we will only need to change this microservice.

### Input

This section explains who is going to access the endpoints.

* `/login` &rarr; This endpoint will receive all the requests that don't include a `JWT Token` and are trying to get one for future secure communications. Requests should include:
  * `email`
  * `username`
* `/user/exists/{username}` &rarr; This endpoint will receive all the request that want to check that verify the existence of a user. Request should include:
  * `username`
  * `token`
* `/user/register` &rarr; This endpoint will receive all the request that want to register a new user in the IAM. Requests should include: 
  * `email`
  * `username`
  * `enabled `
  * `credentials`
    * `type`
    * `value`
    * `temporary`

### Transformation

The requests received will be transformed so they can be understood by our [current IAM provider](https://access.redhat.com/products/identity-management).

* `/user/exists/{username}` &rarr; This request will result in a user existance check.
* `/user/register` &rarr; This request will result in a user add operation.

### Output

All callers should expect a `500 SERVER ERROR` response for internal errors and a `400 BAD REQUEST` for incomplete requests. If there are no errors, the following responses are expected:

* `/user/exists/{username}` &rarr; If the user exists, a `204 NO CONTENT` response will be returned. If the user does not exist, it will return a `404 NOT FOUND` response.
* `/user/register` &rarr; If the user was correctly created a `200 OK` response, if it already exists a `409 CONFLICT` response will be returned.

## IAM operations

This section depends strictly on the [current IAM provider](https://access.redhat.com/products/identity-management).

### User check existance

To execute this operation we need the following REST request to be sent:

```
GET /auth/admin/realms/{realms}/users/{id}
```

Then we need to evaluate the response, it might contain the user data in the `json` body.

### User add

To execute this operation we need the following REST request to be sent:

```
POST /user/register
```

```json
{"username":"alice",
 "email":"alice@alice.com", 
 "credentials":[{"type":"password","value":"alice","temporary"=false}],
 "enabled":"true"
}
```



## Testing

### User check existance

To check it's behavior just run the following `curls`:

These ones will return a  :

```bash
curl -X GET \
  http://localhost:9090/auth/admin/realms/master/users?username="alice" \
  -H 'Authorization: Bearer $token' \ -v                        
```

Curl's valid response:

```
[{"id":"56b55ded-fa60-4d97-b04b-b1579d7d2af7","createdTimestamp":1585742824508,"username":"alice","enabled":true,"totp":false,"emailVerified":false,"firstName":"Alice","lastName":"Alice","email":"alice@alice.com","disableableCredentialTypes":[],"requiredActions":[],"notBefore":0,"access":{"manageGroupMembership":true,"view":true,"mapRoles":true,"impersonate":true,"manage":true}}]
```

If the token has expired we will get this response:

```
{"error":"HTTP 401 Unauthorized"}
```



### User add

```bash
curl --request POST "http://localhost:8080/user/register" \
--header "Content-Type: application/json" \
--data '{"username":"alice","email":"alice@alice.es", "credentials":[{"type":"password","value":"alice","temporary":false}], "enabled":"true"}' -v                     
```

Curl's valid response:

```
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

If the user already exists :

```bash
curl --request POST "http://localhost:8080/user/register" \
--header "Content-Type: application/json" \
--data '{"username":"alice","email":"alice@alice.es", "credentials":[{"type":"password","value":"alice","temporary":false}], "enabled":"true"}' -v                     
```

Curl's valid response :

```
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
< HTTP/1.1 500 Internal Server Error
< Content-Length: 0
< 
* Connection #0 to host localhost left intact
```



If the username is missing:

```bash
curl --request POST "http://localhost:8080/user/register" \
--header "Content-Type: application/json" \
--data '{"email":"alice@alice.es", "credentials":[{"type":"password","value":"alice","temporary":false}], "enabled":"true"}' -v                     
```

Curl's valid response:

```
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





## More info

* [User management API docs](https://www.keycloak.org/docs-api/9.0/rest-api/index.html#_users_resource)
* [User existance check](https://stackoverflow.com/questions/52726048/keycloak-how-to-check-if-username-and-email-exists)
* [User creation](https://stackoverflow.com/questions/52440546/create-user-on-keycloack-from-curl-command)
* [curl example of user creation](https://issues.redhat.com/browse/KEYCLOAK-5383)

curl -v http://localhost:9090/auth/admin/realms/master/users?username=alice&email=alice@alice.com