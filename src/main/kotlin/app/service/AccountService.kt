package app.service

import app.repository.AccountEntity
import app.repository.AccountRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.math.BigDecimal

// В этом классе (слой логики) не должно быть Entity и Dto, только Vo
@Service
class AccountService(
    @Autowired
    private val accountRepository: AccountRepository
) {

    fun getAccount(accountId: String) =
        accountRepository.findById(accountId)

    @Transactional
    // Для PostgreSQL по-умолчанию изоляция READ_COMMITTED
    fun transfer(sourceAccountId: String, destinationAccountId: String, transferAmount: BigDecimal) {
        val sourceAccount: AccountEntity
        val destinationAccount: AccountEntity

        // Если не выбирать порядок блокировки записей будет дедлок
        // java.lang.IllegalStateException: Transaction 5 has been chosen as a deadlock victim. Details:
        // Transaction 2 attempts to update map <table.7> entry with key <1> modified by transaction 5(7263) OPEN 1
        // Transaction 5 attempts to update map <table.7> entry with key <2> modified by transaction 2(7271) OPEN 1
        when {
            sourceAccountId == destinationAccountId ->
                throw IllegalStateException()
            // Даже если в другом потоке могут быть другие инстансы сущностей, номера счетов не будут меняться ни когда
            sourceAccountId < destinationAccountId -> {
                sourceAccount = selectForUpdate(sourceAccountId)
                destinationAccount = selectForUpdate(destinationAccountId)
            }
            else -> {
                destinationAccount = selectForUpdate(destinationAccountId)
                sourceAccount = selectForUpdate(sourceAccountId)
            }
        }

        val decreasedSourceBalance = sourceAccount.balance - transferAmount
        if (decreasedSourceBalance >= BigDecimal.ZERO) {
            accountRepository.save(sourceAccount.apply { balance = decreasedSourceBalance })
            accountRepository.save(destinationAccount.apply { balance += transferAmount })
        } else {
            throw IllegalStateException("Not enough money")
        }
    }

    @Transactional
    fun fund(destinationAccountId: String, amount: BigDecimal) {
        val destinationAccount = selectForUpdate(destinationAccountId)
        accountRepository.save(destinationAccount.apply { balance += amount })
    }

    @Transactional
    fun withdrawal(sourceAccountId: String, amount: BigDecimal) {
        val sourceAccount = selectForUpdate(sourceAccountId)
        val decreasedSourceBalance = sourceAccount.balance - amount
        if (decreasedSourceBalance >= BigDecimal.ZERO) {
            accountRepository.save(sourceAccount.apply { balance = decreasedSourceBalance })
        } else {
            throw IllegalStateException("Not enough money")
        }
    }

    private fun selectForUpdate(accountId: String) =
        accountRepository.findByIdForUpdate(accountId)
            .orElseGet {
                throw IllegalArgumentException("Account with id $accountId is not found.")
            }
}