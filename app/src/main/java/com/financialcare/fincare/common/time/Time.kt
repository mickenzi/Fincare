package com.financialcare.fincare.common.time

import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

fun OffsetDateTime.toLong(): Long {
    return this.atZoneSameInstant(ZoneOffset.UTC).toInstant().toEpochMilli()
}

fun Long.toTime(): OffsetDateTime {
    val offset = OffsetDateTime.now().offset
    return Instant.ofEpochMilli(this).atOffset(offset)
}