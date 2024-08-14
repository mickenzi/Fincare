package com.financialcare.fincare.reports

import com.financialcare.fincare.reports.data.ReportsService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ReportsModule {

    @Binds
    abstract fun bindReportsRepository(reportsService: ReportsService): ReportsRepository
}