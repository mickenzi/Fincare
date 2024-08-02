package com.financialcare.fincare.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.financialcare.fincare.common.identifiable.Identifiable

@Entity(tableName = "expenses", primaryKeys = ["id", "time"])
data class ExpenseEnt(
    override val id: String,
    @ColumnInfo(name = "time")
    val time: Long,
    @ColumnInfo(name = "amount")
    val amount: Long,
    @ColumnInfo(name = "kind")
    val kind: String
) : Identifiable