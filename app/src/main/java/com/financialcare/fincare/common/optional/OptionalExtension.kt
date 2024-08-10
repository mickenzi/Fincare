package com.financialcare.fincare.common.optional

import java.util.Optional

fun <A, T> Optional<A>.fold(ifEmpty: () -> T, ifPresent: (A) -> T): T {
    return this
        .map(ifPresent)
        .orElseGet(ifEmpty)
}