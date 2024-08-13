package com.financialcare.fincare.db.repository

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.YearMonth
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.financialcare.fincare.common.time.toLong
import com.financialcare.fincare.common.time.toTime
import com.financialcare.fincare.db.ExpenseEnt
import io.reactivex.rxjava3.core.Single

@Dao
interface ExpensesDao : ExpensesDBRepository {
    @Transaction
    override fun insert(expense: ExpenseEnt) {
        val year = expense.time.toTime().year
        val month = expense.time.toTime().monthValue
        val offset = OffsetDateTime.now().offset

        val startDate = LocalDate.of(year, month, 1)
        val endDate = YearMonth.of(year, month).atEndOfMonth()

        val st = OffsetDateTime.of(LocalDateTime.of(startDate, LocalTime.MIDNIGHT), offset)
        val et =
            OffsetDateTime.of(LocalDateTime.of(endDate, LocalTime.MIDNIGHT.minusSeconds(1)), offset)

        add(expense)
        editInsert(year, month, expense.amount, kind(st.toLong(), et.toLong()))
    }

    override fun all(id: String?, time: Long?, kinds: Set<String>, pageSize: Int): Single<List<ExpenseEnt>> {
        val offset = OffsetDateTime.now().offset
        val now = YearMonth.now()

        val startDate = LocalDate.of(now.year, now.monthValue, 1)
        val endDate = YearMonth.now().atEndOfMonth()

        val st = OffsetDateTime.of(LocalDateTime.of(startDate, LocalTime.MIDNIGHT), offset)
        val et =
            OffsetDateTime.of(LocalDateTime.of(endDate, LocalTime.MIDNIGHT.minusSeconds(1)), offset)

        return if (kinds.isEmpty()) {
            all(st.toLong(), et.toLong(), id, time, pageSize)
        } else {
            all(st.toLong(), et.toLong(), id, time, kinds, pageSize)
        }
    }

    @Query("DELETE FROM expenses WHERE time < :time")
    override fun deleteAll(time: Long): Single<Unit>

    @Transaction
    override fun delete(expense: ExpenseEnt) {
        val year = expense.time.toTime().year
        val month = expense.time.toTime().monthValue
        val offset = OffsetDateTime.now().offset

        val startDate = LocalDate.of(year, month, 1)
        val endDate = YearMonth.of(year, month).atEndOfMonth()

        val st = OffsetDateTime.of(LocalDateTime.of(startDate, LocalTime.MIDNIGHT), offset)
        val et = OffsetDateTime.of(
            LocalDateTime.of(endDate, LocalTime.MIDNIGHT.minusSeconds(1)),
            offset
        )

        delete(expense.id)
        editDelete(year, month, expense.amount, maxAmount(st.toLong(), et.toLong()), kind(st.toLong(), et.toLong()))
    }

    @Transaction
    @Query("DELETE FROM expenses where id = :id")
    fun delete(id: String)

    @Query(
        "SELECT * FROM expenses where time >= :startTime AND time <= :endTime " +
            "AND (:time IS NULL OR time < :time OR (time = :time AND id > :id )) " +
            "AND (:kinds IS NULL OR kind in (:kinds)) ORDER BY time DESC LIMIT :pageSize"
    )
    fun all(
        startTime: Long,
        endTime: Long,
        id: String?,
        time: Long?,
        kinds: Set<String>?,
        pageSize: Int
    ): Single<List<ExpenseEnt>>

    @Query(
        "SELECT * FROM expenses where time >= :startTime AND time <= :endTime " +
            "AND (:time IS NULL OR time < :time OR (time = :time AND id > :id))" +
            "ORDER BY time DESC LIMIT :pageSize"
    )
    fun all(startTime: Long, endTime: Long, id: String?, time: Long?, pageSize: Int): Single<List<ExpenseEnt>>

    @Query("SELECT * FROM expenses")
    override fun all(): Single<List<ExpenseEnt>>

    @Insert
    fun add(expense: ExpenseEnt)

    @Transaction
    @Query(
        "UPDATE budgets " +
            "SET max_expense_amount = CASE WHEN (:debt >= max_expense_amount OR max_expense_amount IS NULL) " +
            "THEN :debt ELSE max_expense_amount END," +
            " debt = debt + :debt, max_expense_kind = :kind WHERE year = :year AND month = :month"
    )
    fun editInsert(year: Int, month: Int, debt: Long, kind: String)

    @Transaction
    @Query(
        "UPDATE budgets " +
            "SET max_expense_amount = :maxAmount, debt = debt - :amount, max_expense_kind = :kind" +
            " WHERE year = :year AND month = :month"
    )
    fun editDelete(year: Int, month: Int, amount: Long, maxAmount: Long?, kind: String)

    @Transaction
    @Query(
        "SELECT kind FROM expenses " +
            "WHERE time >= :startTime AND time <= :endTime GROUP BY kind ORDER BY SUM(amount) DESC LIMIT 1"
    )
    fun kind(startTime: Long, endTime: Long): String

    @Transaction
    @Query("SELECT MAX(amount) FROM expenses WHERE time >+ :startTime AND time <= :endTime")
    fun maxAmount(startTime: Long, endTime: Long): Long?
}