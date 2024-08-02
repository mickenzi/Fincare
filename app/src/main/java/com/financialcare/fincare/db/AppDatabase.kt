package com.financialcare.fincare.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.financialcare.fincare.db.repository.BudgetsDao
import com.financialcare.fincare.db.repository.ExpensesDao

@Database(entities = [ExpenseEnt::class, BudgetEnt::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expensesDao(): ExpensesDao
    abstract fun budgetsDao(): BudgetsDao
}