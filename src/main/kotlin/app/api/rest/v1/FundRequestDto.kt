package app.api.rest.v1

import java.math.BigDecimal

class FundRequestDto(
    val destinationAccountId: String,
    val amount: BigDecimal
)