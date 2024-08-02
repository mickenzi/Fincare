package com.financialcare.fincare.expenses

import java.time.OffsetDateTime

data class PaginationParams(
    val lastId: String?,
    val lastTime: OffsetDateTime?,
    val pageSize: Int = DEFAULT_PAGE_SIZE
) {
    companion object {
        const val DEFAULT_PAGE_SIZE = 10
        val empty = PaginationParams(null, null, DEFAULT_PAGE_SIZE)
    }
}