package com.financialcare.fincare.db.repository

import com.financialcare.fincare.db.BudgetEnt
import io.reactivex.rxjava3.core.Single

interface ReportsDBRepository {
    fun monthly(year: Int): Single<List<BudgetEnt>>
}