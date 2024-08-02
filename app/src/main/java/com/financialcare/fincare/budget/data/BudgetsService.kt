package com.financialcare.fincare.budget.data

import java.time.YearMonth
import javax.inject.Inject
import com.financialcare.fincare.budget.Budget
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
            budget.maxExpenseDebt,
            budget.maxExpenseKind
        )

        return budgetsDBRepository
            .insert(entity)
            .observeOn(Schedulers.computation())
            .onErrorResumeNext {
                Single.error(it)
            }
            .subscribeOn(Schedulers.io())
    }

    override fun year(year: Int): Single<List<Budget>> {
        return budgetsDBRepository
            .year(year)
            .observeOn(Schedulers.computation())
            .map { it.map(::toBudget) }
            .onErrorResumeNext {
                Single.error(it)
            }
            .subscribeOn(Schedulers.io())
    }

    override fun month(month: Int): Single<Budget> {
        return budgetsDBRepository
            .month(month)
            .observeOn(Schedulers.computation())
            .map(::toBudget)
            .onErrorResumeNext {
                Single.error(it)
            }
            .subscribeOn(Schedulers.io())
    }

    private fun toBudget(budget: BudgetEnt): Budget {
        return Budget(
            income = budget.income,
            debt = budget.debt,
            balance = budget.income - budget.debt,
            month = YearMonth.of(budget.year, budget.month),
            maxExpenseDebt = budget.maxExpenseDebt,
            maxExpenseKind = budget.maxExpenseKind
        )
    }
}