package com.financialcare.fincare.reports.data

import java.time.YearMonth
import javax.inject.Inject
import com.financialcare.fincare.db.BudgetEnt
import com.financialcare.fincare.db.repository.ReportsDBRepository
import com.financialcare.fincare.reports.Budget
import com.financialcare.fincare.reports.ReportsError
import com.financialcare.fincare.reports.ReportsRepository
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class ReportsService @Inject constructor(
    private val reportsDBRepository: ReportsDBRepository
) : ReportsRepository {
    override fun monthly(year: Int): Single<List<Budget>> {
        return reportsDBRepository
            .monthly(year)
            .observeOn(Schedulers.computation())
            .map { it.map(::toBudget) }
            .onErrorResumeNext {
                Single.error(ReportsError.Unknown(it.message ?: "Unknown error."))
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