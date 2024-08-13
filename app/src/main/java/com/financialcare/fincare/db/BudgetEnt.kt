package com.financialcare.fincare.db

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "budgets", primaryKeys = ["year", "month"])
data class BudgetEnt(
    @ColumnInfo(name = "income")
    val income: Long,
    @ColumnInfo(name = "debt")
    val debt: Long,
    @ColumnInfo(name = "year")
    val year: Int,
    @ColumnInfo(name = "month")
    val month: Int,
    @ColumnInfo(name = "max_expense_amount")
    val maxExpenseAmount: Long?,
    @ColumnInfo(name = "max_expense_kind")
    val maxExpenseKind: String?
)