package com.financialcare.fincare.reports

import io.reactivex.rxjava3.core.Single

interface ReportsRepository {
    fun monthly(year: Int): Single<List<Budget>>
}