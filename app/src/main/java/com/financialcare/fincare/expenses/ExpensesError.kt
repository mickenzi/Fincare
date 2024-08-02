package com.financialcare.fincare.expenses

sealed class ExpensesError(msg: String) : Throwable(msg) {
    data class Unknown(val msg: String) : ExpensesError(msg)
}