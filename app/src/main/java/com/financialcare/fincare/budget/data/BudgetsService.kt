package com.financialcare.fincare.budget.data

import java.time.YearMonth
import java.util.Optional
import javax.inject.Inject
import androidx.room.rxjava3.EmptyResultSetException
import com.financialcare.fincare.budget.Budget
import com.financialcare.fincare.budget.BudgetsError
import com.financialcare.fincare.budget.BudgetsRepository
import com.financialcare.fincare.db.BudgetEnt
import com.financialcare.fincare.db.repository.BudgetsDBRepository
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class BudgetsService @Inject constructor(
    private val budgetsDBRepository: BudgetsDBRepository
) : BudgetsRepository {
    override fun add(budget: Budget): Single<Unit> {
        val entity = BudgetEnt(
            budget.income,
            budget.debt,
            budget.month.year,
            budget.month.monthValue,
            budget.maxExpenseAmount,
            budget.maxExpenseKind
        )

        return budgetsDBRepository
            .insert(entity)
            .observeOn(Schedulers.computation())
            .onErrorResumeNext {
                Single.error(BudgetsError.Unknown(it.message ?: "Unknown error."))
            }
            .subscribeOn(Schedulers.io())
    }

    override fun edit(year: Int, month: Int, income: Long): Single<Unit> {
        return budgetsDBRepository
            .edit(year, month, income)
            .observeOn(Schedulers.computation())
            .onErrorResumeNext {
                Single.error(BudgetsError.Unknown(it.message ?: "Unknown error."))
            }
            .subscribeOn(Schedulers.io())
    }

    override fun year(year: Int): Single<List<Budget>> {
        return budgetsDBRepository
            .year(year)
            .observeOn(Schedulers.computation())
            .map { it.map(::toBudget) }
            .onErrorResumeNext {
                Single.error(BudgetsError.Unknown(it.message ?: "Unknown error."))
            }
            .subscribeOn(Schedulers.io())
    }

    override fun month(month: Int): Single<Optional<Budget>> {
        return budgetsDBRepository
            .month(month)
            .observeOn(Schedulers.computation())
            .map { Optional.of(toBudget(it)) }
            .onErrorResumeNext {
                if (it is EmptyResultSetException) {
                    Single.just(Optional.empty())
                } else {
                    Single.error(BudgetsError.Unknown(it.message ?: "Unknown error."))
                }
            }
            .subscribeOn(Schedulers.io())
    }

    private fun toBudget(budget: BudgetEnt): Budget {
        return Budget(
            income = budget.income,
            debt = budget.debt,
            balance = budget.income - budget.debt,
            month = YearMonth.of(budget.year, budget.month),
            maxExpenseAmount = budget.maxExpenseAmount,
            maxExpenseKind = budget.maxExpenseKind
        )
    }
}