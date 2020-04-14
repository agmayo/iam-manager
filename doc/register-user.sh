# Registering a user with username=alice.
curl --request POST "http://localhost:8080/user/register" \
--header "Content-Type: application/json" \
--data '{"username":"alice","email":"alice@alice.es", "credentials":[{"type":"password","value":"alice","temporary":false}], "enabled":"true"}' -v


