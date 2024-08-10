package com.financialcare.fincare.budget

sealed class BudgetsError(msg: String) : Throwable(msg) {
    data class Unknown(val msg: String) : BudgetsError(msg)
}