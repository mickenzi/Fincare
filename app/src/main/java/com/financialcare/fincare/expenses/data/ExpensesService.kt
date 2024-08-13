package com.financialcare.fincare.expenses.data

import java.time.OffsetDateTime
import java.util.UUID
import javax.inject.Inject
import com.financialcare.fincare.common.time.toLong
import com.financialcare.fincare.common.time.toTime
import com.financialcare.fincare.db.ExpenseEnt
import com.financialcare.fincare.db.repository.ExpensesDBRepository
import com.financialcare.fincare.expenses.Expense
import com.financialcare.fincare.expenses.ExpensesError
import com.financialcare.fincare.expenses.ExpensesRepository
import com.financialcare.fincare.expenses.FilterParams
import com.financialcare.fincare.expenses.PaginationParams
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class ExpensesService @Inject constructor(
    private val expensesDBRepository: ExpensesDBRepository
) : ExpensesRepository {
    override fun add(time: OffsetDateTime, amount: Long, kind: String): Single<Unit> {
        val entity = ExpenseEnt(
            id = UUID.randomUUID().toString(),
            time = time.toLong(),
            amount = amount,
            kind = kind
        )

        return Single
            .fromCallable { expensesDBRepository.insert(entity) }
            .observeOn(Schedulers.computation())
            .onErrorResumeNext {
                Single.error(ExpensesError.Unknown(it.message ?: "Unknown error."))
            }
            .subscribeOn(Schedulers.io())
    }

    override fun all(pageParams: PaginationParams, filterParams: FilterParams): Single<List<Expense>> {
        return expensesDBRepository
            .all(
                pageParams.lastId,
                pageParams.lastTime?.toLong(),
                filterParams.kinds,
                pageParams.pageSize
            )
            .observeOn(Schedulers.computation())
            .map { it.map(::toExpense) }
            .onErrorResumeNext {
                Single.error(ExpensesError.Unknown(it.message ?: "Unknown error."))
            }
            .subscribeOn(Schedulers.io())
    }

    override fun delete(expense: Expense): Single<Unit> {
        val entity = ExpenseEnt(
            id = expense.id,
            time = expense.time.toLong(),
            amount = expense.amount,
            kind = expense.kind
        )

        return Single
            .fromCallable { expensesDBRepository.delete(entity) }
            .observeOn(Schedulers.computation())
            .onErrorResumeNext {
                Single.error(ExpensesError.Unknown(it.message ?: "Unknown error."))
            }
            .subscribeOn(Schedulers.io())
    }

    override fun deleteAll(time: OffsetDateTime): Single<Unit> {
        return expensesDBRepository.deleteAll(time.toLong())
            .observeOn(Schedulers.computation())
            .onErrorResumeNext {
                Single.error(ExpensesError.Unknown(it.message ?: "Unknown error."))
            }
            .subscribeOn(Schedulers.io())
    }

    private fun toExpense(expense: ExpenseEnt): Expense {
        return Expense(
            id = expense.id,
            time = expense.time.toTime(),
            amount = expense.amount,
            kind = expense.kind
        )
    }
}