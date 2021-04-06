# этого хватает, чтобы воспроизвести дедлок
while true; do
  curl localhost:8080/application/transaction/transfer -H "Content-Type: application/json" -d '{"sourceAccountId":"1", "destinationAccountId":"2", "transferAmount":"0.01"}' &
  curl localhost:8080/application/transaction/transfer -H "Content-Type: application/json" -d '{"sourceAccountId":"2", "destinationAccountId":"1", "transferAmount":"0.01"}' &
done