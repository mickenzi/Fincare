package com.financialcare.fincare.budget

import com.financialcare.fincare.budget.data.BudgetsService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class BudgetsModule {

    @Binds
    abstract fun bindBudgetsRepository(budgetService: BudgetsService): BudgetsRepository
}