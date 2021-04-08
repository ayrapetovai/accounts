package app.api.rest.v1

import app.service.AccountService
import org.springframework.web.bind.annotation.*

// Здесь должно быть перекладываение из dto в vo
@RestController
class AccountController(
    private val accountService: AccountService
) {
    @GetMapping("account/{accountId}", produces = ["application/json"])
    fun getAccount(
        @PathVariable("accountId") accountId: String
    ) = accountService.getAccount(accountId)

    @PostMapping("transaction/transfer")
    fun transfer(
        @RequestBody transferRequest: TransferRequestDto
    ) {
        accountService.transfer(
            transferRequest.sourceAccountId,
            transferRequest.destinationAccountId,
            transferRequest.transferAmount)
    }

    @PostMapping("transaction/fund")
    fun fund(
        @RequestBody fundRequest: FundRequestDto
    ) {
        accountService.fund(fundRequest.destinationAccountId, fundRequest.amount)
    }

    @PostMapping("transaction/withdrawal")
    fun withdrawal(
        @RequestBody withdrawalRequest: WithdrawalRequestDto
    ) {
        accountService.withdrawal(withdrawalRequest.sourceAccountId, withdrawalRequest.amount)
    }
}