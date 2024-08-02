package com.financialcare.fincare.db.repository

import com.financialcare.fincare.db.ExpenseEnt
import io.reactivex.rxjava3.core.Single

interface ExpensesDBRepository {
    fun deleteAll(time: Long): Single<Unit>
    fun delete(expense: ExpenseEnt)
    fun all(id: String?, time: Long?, kinds: Set<String>, pageSize: Int): Single<List<ExpenseEnt>>
    fun all(): Single<List<ExpenseEnt>>
    fun insert(expense: ExpenseEnt)
}