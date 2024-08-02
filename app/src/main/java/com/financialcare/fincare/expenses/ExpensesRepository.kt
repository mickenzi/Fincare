package com.financialcare.fincare.expenses

import java.time.OffsetDateTime
import io.reactivex.rxjava3.core.Single

interface ExpensesRepository {
    fun add(time: OffsetDateTime, amount: Long, kind: String): Single<Unit>
    fun all(pageParams: PaginationParams, filterParams: FilterParams): Single<List<Expense>>
    fun delete(expense: Expense): Single<Unit>
    fun deleteAll(time: OffsetDateTime): Single<Unit>
}