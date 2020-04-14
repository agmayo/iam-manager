# iam-manager

Quarkus Microservice that controls all communication with an Identity Access Manager (A service that manages access to all our resources behind the [gateway](https://www.keycloak.org/documentation))

## Endpoints

### User check existance

This endpoint will receive all the request that want to verify the existence of a user. 

- `http://localhost:8080/user/exists/{username}` 

The requests received will be transformed so they can be understood by our [current IAM provider](https://access.redhat.com/products/identity-management).

***Translation:***

```
curl -X GET \
  http://localhost:9090/auth/admin/realms/master/users?username="{username}" \
  -H 'Authorization: Bearer $token' \ -v
```

The response will contain the user data in the `json` body.

### User add

This endpoint will receive all the request that want to check that verify the existence of a user. 

* `http://localhost:8080/user/register` &rarr; This endpoint will receive all the request that want to register a new user in the IAM. Requests should include: 
  * `email`
  * `username`
  * `enabled `
  * `credentials`
    * `type`
    * `value`
    * `temporary`

The requests received will be transformed so they can be understood by our [current IAM provider](https://access.redhat.com/products/identity-management).

***Translation :***

```
curl --request POST "http://localhost:8080/user/register" \
--header "Content-Type: application/json" \
--data '{"username":"alice","email":"alice@alice.es", "credentials":[{"type":"password","value":"alice","temporary":false}], "enabled":"true"}' -v
```

