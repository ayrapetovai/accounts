import app.repository.AccountEntity
import app.repository.AccountRepository
import app.service.AccountService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.math.BigDecimal
import java.util.*

class AccountServiceTest {

    private val accountRepositoryMock = mock(AccountRepository::class.java)
    private val accountService = AccountService(accountRepositoryMock)

    @Test
    fun transferCheckAccountLockOrder() {
        val accountA = "1"
        val accountB = "2"

        val lockOrder = mutableListOf<String>()
        `when`(accountRepositoryMock.findByIdForUpdate(anyString()))
            .thenAnswer {
                val accountId = it.arguments[0] as String
                lockOrder.add(accountId)
                Optional.of(
                    AccountEntity(
                        id = accountId,
                        balance = BigDecimal.TEN
                    )
                )
            }

        accountService.transfer(accountA, accountB, BigDecimal.ONE)
        // check if elements of lockOrder are sorted by <
        assertTrue(lockOrder.zipWithNext { a, b -> a < b }.all { it }, "account lock order is inconsistent")

        lockOrder.clear()

        accountService.transfer(accountB, accountA, BigDecimal.ONE)
        // check if elements of lockOrder are sorted by <
        assertTrue(lockOrder.zipWithNext { a, b -> a < b }.all { it }, "account lock order is inconsistent")
    }

    @Test
    fun transferFromOneAccountToTheSameAccountMustBeNotAllowed() {
        assertThrows(IllegalStateException::class.java) {
            accountService.transfer("1", "1", BigDecimal.ONE)
        }
    }

    @Test
    fun transferMustTakeOnlyPositiveAmount() {
        assertThrows(IllegalArgumentException::class.java) {
            accountService.transfer("1", "2", BigDecimal.ONE.unaryMinus())
        }
    }

    @Test
    fun fundMustTakeOnlyPositiveAmount() {
        assertThrows(IllegalArgumentException::class.java) {
            accountService.fund("1", BigDecimal.ONE.unaryMinus())
        }
    }

    @Test
    fun withdrawalMustTakeOnlyPositiveAmount() {
        assertThrows(IllegalArgumentException::class.java) {
            accountService.withdrawal("1", BigDecimal.ONE.unaryMinus())
        }
    }
}