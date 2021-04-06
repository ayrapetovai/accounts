curl localhost:8080/application/account/1
curl localhost:8080/application/account/2
curl localhost:8080/application/transaction/transfer -H "Content-Type: application/json" -d '{"sourceAccountId":"1", "destinationAccountId":"2", "transferAmount":"40.00"}'
curl localhost:8080/application/transaction/withdrawal -H "Content-Type: application/json" -d '{"sourceAccountId":"2", "amount":"81.00"}'
curl localhost:8080/application/transaction/fund -H "Content-Type: application/json" -d '{"destinationAccountId":"2", "amount":"30.00"}'