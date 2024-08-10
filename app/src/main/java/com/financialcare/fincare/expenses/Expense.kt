package com.financialcare.fincare.expenses

import java.time.OffsetDateTime
import com.financialcare.fincare.common.identifiable.Identifiable

data class Expense(
    override val id: String,
    val time: OffsetDateTime,
    val amount: Long,
    val kind: String
) : Identifiable