package com.financialcare.fincare.budget

import java.util.Optional
import io.reactivex.rxjava3.core.Single

interface BudgetsRepository {
    fun add(budget: Budget): Single<Unit>
    fun edit(year: Int, month: Int, income: Long): Single<Unit>
    fun month(month: Int): Single<Optional<Budget>>
}