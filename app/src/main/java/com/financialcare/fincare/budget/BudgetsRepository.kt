package com.financialcare.fincare.budget

import io.reactivex.rxjava3.core.Single

interface BudgetsRepository {
    fun add(budget: Budget): Single<Unit>
    fun month(month: Int): Single<Budget>
    fun year(year: Int): Single<List<Budget>>
}