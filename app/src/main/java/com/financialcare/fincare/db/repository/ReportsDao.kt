package com.financialcare.fincare.db.repository

import androidx.room.Dao
import androidx.room.Query
import com.financialcare.fincare.db.BudgetEnt
import io.reactivex.rxjava3.core.Single

@Dao
interface ReportsDao : ReportsDBRepository {
    @Query("SELECT * FROM budgets where year = :year")
    override fun monthly(year: Int): Single<List<BudgetEnt>>
}