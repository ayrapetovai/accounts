package app.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.QueryHints
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*
import javax.persistence.LockModeType
import javax.persistence.QueryHint

@Repository
interface AccountRepository: JpaRepository<AccountEntity, String> {
    // Если ты вызывал select for update без "обрамляющей" транзакции - ты ошибся, потому,
    // что ты хочешь менять данные в той же транзакции, в которой select for update.
    @Transactional(propagation = Propagation.MANDATORY)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(QueryHint(name = "javax.persistence.lock.timeout", value = "3000"))
    @Query("from AccountEntity where id = :accountId")
    fun findByIdForUpdate(accountId: String): Optional<AccountEntity> = findById(accountId)
}