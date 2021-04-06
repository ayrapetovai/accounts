package app.api.rest.v1

import java.math.BigDecimal

class TransferRequestDto(
    val sourceAccountId: String,
    val destinationAccountId: String,
    val transferAmount: BigDecimal
)