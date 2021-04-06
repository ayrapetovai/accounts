package app.repository

import java.math.BigDecimal
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "account")
class AccountEntity(
    @Id
    val id: String,
    var balance: BigDecimal,
)