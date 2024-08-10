package com.financialcare.fincare.db.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.financialcare.fincare.db.BudgetEnt
import io.reactivex.rxjava3.core.Single

@Dao
interface BudgetsDao : BudgetsDBRepository {
    @Insert
    override fun insert(budgetEnt: BudgetEnt): Single<Unit>

    @Query("UPDATE budgets SET income = :income WHERE year = :year AND month = :month")
    override fun edit(year: Int, month: Int, income: Long): Single<Unit>

    @Query("SELECT * FROM budgets WHERE month = :month")
    override fun month(month: Int): Single<BudgetEnt>

    @Query("SELECT * FROM budgets WHERE year = :year")
    override fun year(year: Int): Single<List<BudgetEnt>>
}