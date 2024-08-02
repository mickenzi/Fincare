package com.financialcare.fincare.expenses

data class FilterParams(
    val kinds: Set<String>
) {
    companion object {
        val empty = FilterParams(emptySet())
    }
}
