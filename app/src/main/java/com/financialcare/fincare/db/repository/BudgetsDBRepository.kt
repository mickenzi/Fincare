package com.financialcare.fincare.db.repository

import com.financialcare.fincare.db.BudgetEnt
import io.reactivex.rxjava3.core.Single

interface BudgetsDBRepository {
    fun insert(budgetEnt: BudgetEnt): Single<Unit>
    fun edit(year: Int, month: Int, income: Long): Single<Unit>
    fun month(month: Int): Single<BudgetEnt>
}