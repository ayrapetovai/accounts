package app.api.rest.v1

import java.math.BigDecimal

class WithdrawalRequestDto (
    val sourceAccountId: String,
    val amount: BigDecimal
)