# Check if a user with username=Alice exists.
curl -X GET \
  http://localhost:9090/auth/admin/realms/master/users?username="alice" \
  -H 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJUTS10a2JtZWxkZGF0WFVWcnRTZzdRVW02QXJFNWYzOW5Sc3IycDdMbXlVIn0.eyJleHAiOjE1ODY4OTEzODgsImlhdCI6MTU4Njg1NTM4OCwianRpIjoiMWM0NmVkZmEtZjRhOS00MDJmLWJmZTItYjJkNDI4OTQ1Njc3IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo5MDkwL2F1dGgvcmVhbG1zL21hc3RlciIsInN1YiI6IjY2N2RmNTZlLTMxYTQtNDgwZi1hNjc2LTk5YTFlNTI3Y2E0YSIsInR5cCI6IkJlYXJlciIsImF6cCI6ImFkbWluLWNsaSIsInNlc3Npb25fc3RhdGUiOiI4MzUxODMxZS01YWE1LTQ5NGEtYmNlYi0xMGFkZDRhNDIyYjUiLCJhY3IiOiIxIiwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJhZG1pbiJ9.P7YBU0LOp0D-iNmm33JUuV9hbg0P8W9A8P4RHKKfLBzSo3cpCDPmlfAaEHFiKTYro1TC51oUkuxvYIZI6JqkaQ18jM3IcyEOjxA0a-tKG6ATvQ_GH3atvvLeNbefCTdJobav3sBQwaeQO7hSJeAoKQfEkJxbfpY3GJFQPS9neJi1eerKmIugVefWtarj5jAC_Ibe0pfTKsTB7Ufu8t_sm162VG1DdRDBXVhQR3-2K4b-EoJ7L4PfpzZQVmxYorxG6xalV7gMn0VpdKmq0OR0FJcqM9py-HQGS_6SzYqNHr3OzbfMmv0R2LuEPvdFmNKo2ctnqq3m5SLa3if9Pjt1Cw' \ -v


