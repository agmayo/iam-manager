curl --request POST "http://localhost:9090/auth/realms/master/protocol/openid-connect/token" \
--header "Content-Type: application/x-www-form-urlencoded" \
--header 'Authorization: Basic YXBwLWF1dGh6LXJlc3Qtc3ByaW5nYm9vdDpzZWNyZXQ=' \
-d "client_id=admin-cli" \
-d "grant_type=password" \
-d "username=admin" \
-d "password=Pa55w0rd" -v

