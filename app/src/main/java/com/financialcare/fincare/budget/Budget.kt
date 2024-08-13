package com.financialcare.fincare.budget

import java.time.YearMonth

data class Budget(
    val income: Long = 0,
    val debt: Long = 0,
    val balance: Long = 0,
    val month: YearMonth,
    val maxExpenseAmount: Long? = null,
    val maxExpenseKind: String? = null
)