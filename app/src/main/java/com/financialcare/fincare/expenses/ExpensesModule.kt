package com.financialcare.fincare.expenses

import com.financialcare.fincare.expenses.data.ExpensesService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ExpensesModule {

    @Binds
    abstract fun bindExpensesRepository(expensesService: ExpensesService): ExpensesRepository
}