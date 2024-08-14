package com.financialcare.fincare.db

import javax.inject.Singleton
import android.content.Context
import androidx.room.Room
import com.example.fincare.BuildConfig
import com.financialcare.fincare.db.repository.BudgetsDBRepository
import com.financialcare.fincare.db.repository.ExpensesDBRepository
import com.financialcare.fincare.db.repository.ReportsDBRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DBModule {
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room
            .databaseBuilder(
                context,
                AppDatabase::class.java,
                BuildConfig.DB
            )
            .build()
    }

    @Singleton
    @Provides
    fun provideBudgetsDBRepository(appDatabase: AppDatabase): BudgetsDBRepository {
        return appDatabase.budgetsDao()
    }

    @Singleton
    @Provides
    fun provideExpensesDBRepository(appDatabase: AppDatabase): ExpensesDBRepository {
        return appDatabase.expensesDao()
    }

    @Singleton
    @Provides
    fun provideReportsDBRepository(appDatabase: AppDatabase): ReportsDBRepository {
        return appDatabase.reportsDao()
    }
}