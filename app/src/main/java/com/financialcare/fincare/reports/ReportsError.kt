package com.financialcare.fincare.reports

sealed class ReportsError(msg: String) : Throwable(msg) {
    data class Unknown(val msg: String) : ReportsError(msg)
}