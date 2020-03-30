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
* `/register` &rarr; This endpoint will receive all the request that want to register a new user in the IAM. Requests should include: 
  * `email`
  * `username`
  * `password`

### Transformation

The requests received will be transformed so they can be understood by our [current IAM provider](https://access.redhat.com/products/identity-management).

* `/login` &rarr; This request will result in a user existance check.
* `/register` &rarr; This reques will result in a user add operation.

### Output

All callers should expect a `500 SERVER ERROR` response for internal errors and a `400 BAD REQUEST` for incomplete requests. If there are no errors, the following responses are expected:

* `/login` &rarr; If the user exists, a `204 NO CONTENT` response will be returned. If the user does not exist, it will return a `404 NOT FOUND` response.
* `/register` &rarr; If the user was correctly created a `200 OK` response, if it already exists a `409 CONFLICT` response will be returned.

## IAM operations

This section depends strictly on the [current IAM provider](https://access.redhat.com/products/identity-management).

### User check existance

To execute this operation we need the following REST request to be sent:

```
GET /{realm}/users?username=rpradom&email=rpradom@minsait.com
```

Then we need to evaluate the response, it might contain the user data in the `json` body.

### User add

To execute this operation we need the following REST request to be sent:

```
POST /{realm}/users
```

```json
{"firstName":"xyz",
 "lastName":"xyz", 
 "email":"demo2@gmail.com", 
 "enabled":"true",
 "credentials":[{"type":"password","value":"test1"}]
}
```

## More info

* [User management API docs](https://www.keycloak.org/docs-api/9.0/rest-api/index.html#_users_resource)
* [User existance check](https://stackoverflow.com/questions/52726048/keycloak-how-to-check-if-username-and-email-exists)
* [User creation](https://stackoverflow.com/questions/52440546/create-user-on-keycloack-from-curl-command)
* [curl example of user creation](https://issues.redhat.com/browse/KEYCLOAK-5383)

curl -v http://localhost:9090/auth/admin/realms/master/users?username=alice&email=alice@alice.com